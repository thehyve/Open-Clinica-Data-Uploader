package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
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

    public MetaDataTree getMetadataTree(UploadSession submission, String ocwsHash) throws Exception {
        MetaData metaData = getMetaData(submission, ocwsHash);
        MetaDataTree tree = buildTree(metaData);
        return tree;
    }

    private MetaDataTree buildTree(MetaData metaData) {
        String studyIdentifier = metaData.getStudyIdentifier();
        MetaDataTree root = new MetaDataTree();
        root.setValue(studyIdentifier);
        List<EventDefinition> eventDefinitions = metaData.getEventDefinitions();
        List<MetaDataTree> studyChildren = new ArrayList<>();
        eventDefinitions.stream().forEach(eventDefinition -> {
            String studyEventOID = eventDefinition.getStudyEventOID();
            MetaDataTree eventNode = new MetaDataTree();
            eventNode.setValue(studyEventOID);
            studyChildren.add(eventNode);

            List<MetaDataTree> eventChildren = new ArrayList<>();
            eventNode.setChildren(eventChildren);
            eventDefinition.getCrfDefinitions().stream().forEach(crfDefinition -> {
                MetaDataTree crfNode = new MetaDataTree();
                crfNode.setValue(crfDefinition.getOid());
                eventChildren.add(crfNode);
                List<MetaDataTree> crfChildren = new ArrayList<>();
                crfNode.setChildren(crfChildren);
                //crfDefinition.g

            });

        });
        return root;
    }

    @Autowired
    OpenClinicaService openClinicaService;

    public MetaData getMetaData(UploadSession submission, String ocwsHash) throws Exception {
        OcUser owner = submission.getOwner();
        FieldsDetermined info = getInfo(submission);
        Study study = new Study(info.getStudy(), info.getStudy(), info.getStudy());
        return openClinicaService.getMetadata(owner.getUsername(), ocwsHash, owner.getOcEnvironment(), study);
    }


    public class FieldsDetermined {
        private String study;
        private String eventname;
        private String crfName;
        private String crfVersion;

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
        private String value;
        private MetaDataTree parent;
        private List<MetaDataTree> children;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<MetaDataTree> getChildren() {
            return children;
        }

        public void setChildren(List<MetaDataTree> children) {
            this.children = children;
        }
    }
}
