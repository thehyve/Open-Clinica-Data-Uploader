package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.MetaDataTree;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcTreePath;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for all (user submitted) ClinicalData related manipulations.
 *
 * Created by piotrzakrzewski on 07/05/16.
 */
@Service
public class DataService {

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    /**
     * Returns a simple structure containing target path for user submission.
     * i.e. study/event/crf/version.
     * Meant for determining which parts of this path are specified (non-empty)
     *
     * @param submission
     * @return
     */
    public FieldsDetermined getInfo(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        if (bySubmission.size() == 0) return null;
        Set<String> usedStudies = bySubmission.stream().map(clinicalData -> clinicalData.getStudy()).collect(Collectors.toSet());
        FieldsDetermined info = new FieldsDetermined();
        if (usedStudies.contains("")) info.setStudy("");
        else info.setStudy(usedStudies.stream().findFirst().get());
        return info;
    }

    /**
     * Returns original item names submitted by the user. Difference between originalItemName and just item name
     * is that the former is the name as found in the user submitted text file, while just item name is the name that
     * will be used during validation and data upload, this name is changed by the mapping.
     *
     * @param submission
     * @return
     */
    public List<String> getUserItems(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        return bySubmission.stream()
                .map(clinicalData -> clinicalData.getOriginalItem())
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Returns a set of tuples subjectid-event name
     * For purpose of event registration
     *
     * @param submission
     * @return
     */
    public Set<ImmutablePair> getPatientsInEvent(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        Set<ImmutablePair> ret = bySubmission.stream()
                .map(clinicalData -> new ImmutablePair(clinicalData.getSsid(), clinicalData.getEventName() + "#" + clinicalData.getEventRepeat()))
                .collect(Collectors.toSet());
        return ret;
    }

    /**
     * Returns tree built from the subset of the study (which study is inferred from UploadSession).
     * Subset is determined using user selection (see inferSelection method).
     *
     * @param submission
     * @param ocwsHash
     * @return
     * @throws Exception
     */
    public MetaDataTree getMetadataTree(UploadSession submission, String ocwsHash) throws Exception {
        MetaData metaData = getMetaData(submission, ocwsHash);
        MetaDataTree tree = buildTree(metaData);
        OcTreePath selection = inferSelection(submission);
        tree = OcTreePath.filter(tree, selection);
        return tree;
    }

    /**
     * Selection is needed for filtering metadata tree.
     * It is assumed that there is only one CRF and CRF Version in the data file.
     * Adherence to this is checked by validation rules.
     * Therefore here we just take first clinical data variable and retreie its CRF and CRF version.
     *
     * @param submission
     * @return selection
     */
    private OcTreePath inferSelection(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        ClinicalData clinicalData = bySubmission.stream().findFirst().get();
        String crfName = clinicalData.getCrfName();
        String crfVersion = clinicalData.getCrfVersion();
        String eventName = clinicalData.getEventName();
        OcTreePath selection = new OcTreePath();
        selection.setEvent(eventName);
        selection.setCrf(crfName);
        selection.setVersion(crfVersion);
        return selection;
    }

    /**
     * Builds tree from whole study.
     *
     * @param metaData
     * @return
     */
    public static MetaDataTree buildTree(MetaData metaData) { //TODO: move to some utils class? Tree builder?
        String studyIdentifier = metaData.getStudyOID();

        MetaDataTree studyNode = new MetaDataTree();
        studyNode.setName(studyIdentifier);
        List<EventDefinition> eventDefinitions = metaData.getEventDefinitions();
        List<MetaDataTree> studyChildren = new ArrayList<>();
        studyNode.setChildren(studyChildren);
        eventDefinitions.stream().forEach(eventDefinition -> {
            String studyEventName = eventDefinition.getName();
            MetaDataTree eventNode = new MetaDataTree();
            eventNode.setName(studyEventName);
            studyChildren.add(eventNode);

            List<MetaDataTree> eventChildren = new ArrayList<>();

            eventNode.setChildren(eventChildren);
            HashMap<String, MetaDataTree> crfNodes = new HashMap<>();
            eventDefinition.getCrfDefinitions().stream().forEach(crfDefinition -> {
                String crfName = crfDefinition.getName();
                MetaDataTree crfNode;
                List<MetaDataTree> crfChildren;
                if (!crfNodes.containsKey(crfName)) {
                    crfNode = new MetaDataTree();
                    crfNode.setName(crfName);
                    eventChildren.add(crfNode);
                    crfChildren = new ArrayList<>();
                    crfNode.setChildren(crfChildren);
                    crfNodes.put(crfName, crfNode);
                } else {
                    crfNode = crfNodes.get(crfName);
                    crfChildren = crfNode.getChildren();
                }
                String version = crfDefinition.getVersion();
                MetaDataTree versionNode = new MetaDataTree();
                versionNode.setName(version);
                crfChildren.add(versionNode);
                Set<ItemDefinition> items = crfDefinition.allItems();

                List<MetaDataTree> itemNodes = items.stream()
                        .map(itemDefinition -> itemDefinition.getName())
                        .collect(Collectors.toSet())
                        .stream().map(s -> new MetaDataTree(s))
                        .collect(Collectors.toList());
                versionNode.setChildren(itemNodes);
            });

        });
        return studyNode;
    }

    @Autowired
    OpenClinicaService openClinicaService;

    /**
     * Returns study MetaData inferring which OcEnvironment from UploadSession data (along with the user itself)
     *
     * @param submission
     * @param ocwsHash
     * @return
     * @throws Exception
     */
    public MetaData getMetaData(UploadSession submission, String ocwsHash) throws Exception {
        OcUser owner = submission.getOwner();
        FieldsDetermined info = getInfo(submission);
        Study study = findStudy(info.getStudy(), owner, ocwsHash);
        if (study == null) {
            return null;
        }
        return openClinicaService.getMetadata(owner.getUsername(), ocwsHash, owner.getOcEnvironment(), study);
    }

    /**
     * Returns Study object (holding name, idenfier and OID) given study name
     * @param studyName
     * @param owner
     * @param ocwsHash
     * @return
     * @throws Exception
     */
    public Study findStudy(String studyName, OcUser owner, String ocwsHash) throws Exception {
        //TODO: implement caching of studies instead of looking it up each time by WS call
        List<Study> studies = openClinicaService.listStudies(owner.getUsername(), ocwsHash, owner.getOcEnvironment());
        List<Study> matching = studies.stream().filter(study -> study.getName().equals(studyName)).collect(Collectors.toList());
        if (matching.size() == 1) {
            return matching.get(0);
        } else if (matching.size() == 0) {
            return null;
        } else {
            throw new Exception("Multiple studies match name: " + studyName + " fatal data inconsistency.");
        }
    }

    public Collection<String> getTargetCrf(UploadSession submission, String ocwsHash) throws Exception {
        OcTreePath selection = inferSelection(submission);
        MetaDataTree metadataTree = getMetadataTree(submission, ocwsHash, selection);
        if (metadataTree == null) {
            return Collections.emptyList();
        }
        String pathSep = "\\";
        Collection rootPathElements = new ArrayList();
        rootPathElements.add(selection.getEvent());
        rootPathElements.add(selection.getCrf());
        rootPathElements.add(selection.getVersion());
        String rootPath = String.join(pathSep, rootPathElements);
        List<String> targetedPaths = metadataTree.getChildren().stream()
                .map(node -> rootPath + pathSep + node.getName()).collect(Collectors.toList());
        return targetedPaths;
    }

    private MetaDataTree getMetadataTree(UploadSession submission, String ocwsHash, OcTreePath selection) throws Exception {
        MetaData metaData = getMetaData(submission, ocwsHash);
        MetaDataTree tree = buildTree(metaData);
        tree = OcTreePath.filter(tree, selection);
        return tree;
    }


    public class FieldsDetermined {
        private String study = "";
        private String eventname = "";
        private String crfName = "";
        private String crfVersion = "";

        public String getStudy() {
            return study;
        }

        public void setStudy(String study) {
            this.study = study;
        }

        public String getEventname() {
            return eventname;
        }

        public void setEventname(String eventname) {
            this.eventname = eventname;
        }

        public String getCrfName() {
            return crfName;
        }

        public void setCrfName(String crfName) {
            this.crfName = crfName;
        }

        public String getCrfVersion() {
            return crfVersion;
        }

        public void setCrfVersion(String crfVersion) {
            this.crfVersion = crfVersion;
        }
    }
}
