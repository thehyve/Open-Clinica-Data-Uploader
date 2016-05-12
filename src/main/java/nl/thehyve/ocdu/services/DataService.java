package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.repositories.ClinicalDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 07/05/16.
 */
@Service
public class DataService {

    @Autowired
    ClinicalDataRepository clinicalDataRepository;

    public FieldsDetermined getInfo(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        if (bySubmission.size() == 0) return null;
        Set<String> usedStudies = bySubmission.stream().map(clinicalData -> clinicalData.getStudy()).collect(Collectors.toSet());
        FieldsDetermined info = new FieldsDetermined();
        if (usedStudies.contains("")) info.setStudy("");
        else info.setStudy(usedStudies.stream().findFirst().get());
        return info;
    }

    public List<String> getUserItems(UploadSession submission) {
        List<ClinicalData> bySubmission = clinicalDataRepository.findBySubmission(submission);
        return bySubmission.stream()
                .map(clinicalData -> clinicalData.getItem())
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList());
    }

    public MetaDataTree getMetadataTree(UploadSession submission, String ocwsHash) throws Exception {
        MetaData metaData = getMetaData(submission, ocwsHash);
        MetaDataTree tree = buildTree(metaData);
        return tree;
    }

    private MetaDataTree buildTree(MetaData metaData) {
        String studyIdentifier = metaData.getStudyIdentifier();

        MetaDataTree root = new MetaDataTree("root");

        MetaDataTree studyNode = new MetaDataTree();
        root.addChild(studyNode);
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
            eventDefinition.getCrfDefinitions().stream().forEach(crfDefinition -> {
                MetaDataTree crfNode = new MetaDataTree();
                crfNode.setName(crfDefinition.getName());
                eventChildren.add(crfNode);
                List<MetaDataTree> crfChildren = new ArrayList<>();
                crfNode.setChildren(crfChildren);

                String version = crfDefinition.getVersion();
                MetaDataTree versionNode = new MetaDataTree();
                versionNode.setName(version);
                crfChildren.add(versionNode);
                List<ItemDefinition> items = new ArrayList<>();
                crfDefinition.getItemGroups().stream().forEach(itemGroupDefinition -> {
                    items.addAll(itemGroupDefinition.getItems());
                }); //TODO: investigate why items contain duplicated items. GroupDefinitions should not overlap.

                List<MetaDataTree> itemNodes = items.stream()
                        .map(itemDefinition -> itemDefinition.getName())
                        .collect(Collectors.toSet())
                        .stream().map(s -> new MetaDataTree(s))
                        .collect(Collectors.toList());
                versionNode.setChildren(itemNodes);
            });

        });
        return root;
    }

    @Autowired
    OpenClinicaService openClinicaService;

    public MetaData getMetaData(UploadSession submission, String ocwsHash) throws Exception {
        OcUser owner = submission.getOwner();
        FieldsDetermined info = getInfo(submission);
        Study study = findStudy(info.getStudy(), owner, ocwsHash);
        if (study == null) {
            return null;
        }
        return openClinicaService.getMetadata(owner.getUsername(), ocwsHash, owner.getOcEnvironment(), study);
    }

    public Study findStudy(String studyName, OcUser owner, String ocwsHash) throws Exception {
        //TODO: implement caching of studies instead of looking it up each time by WS call
        List<Study> studies = openClinicaService.listStudies(owner.getUsername(), ocwsHash, owner.getOcEnvironment());
        List<Study> matching = studies.stream().filter(study -> study.getName().equals(studyName)).collect(Collectors.toList());
        if (matching.size() == 1) {
            return matching.get(0);
        } else if (matching.size() == 0){
            return null;
        } else {
          throw new Exception("Multiple studies match name: " +studyName+" fatal data inconsistency.");
        }
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

    public class MetaDataTree {
        private String name;
        //private MetaDataTree parent;
        private List<MetaDataTree> children = new ArrayList<>();

        public MetaDataTree(String name) {
            this.name = name;
        }

        public MetaDataTree() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MetaDataTree> getChildren() {
            return children;
        }

        public void setChildren(List<MetaDataTree> children) {
            this.children = children;
        }

        public void addChild(MetaDataTree node) {
            this.children.add(node);
        }
    }
}
