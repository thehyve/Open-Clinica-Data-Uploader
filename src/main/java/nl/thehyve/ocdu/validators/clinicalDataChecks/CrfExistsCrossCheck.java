package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.errors.CRFDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.apache.commons.lang3.StringUtils;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class CrfExistsCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, Map<ClinicalData, ItemDefinition> itemDefMap, List<StudySubjectWithEventsType> studySubjectWithEventsTypeList, Map<ClinicalData, Boolean> shownMap, Map<String, Set<CRFDefinition>> eventMap) {
        List<ClinicalData> crfNotExistsOffenders = getCrfNotExistsOffenders(data, eventMap);
        if (crfNotExistsOffenders.size() > 0) {
            CRFDoesNotExist error = new CRFDoesNotExist();
            List<String> offendingNames = new ArrayList<>();
            crfNotExistsOffenders.stream().forEach(clinicalData -> {
                String crf = clinicalData.getCrfName();
                String version = clinicalData.getCrfVersion();
                if (StringUtils.isBlank(crf)) {
                    crf = "(LEFT BLANK)";
                }
                if (StringUtils.isBlank(version)) {
                    version = "(LEFT BLANK)";
                }
                String eventName = clinicalData.getEventName();

                String msg = "CRF: " + crf + " version: " + version + " in event: " + eventName + " for subject: "
                        + clinicalData.getSsid();
                if (!offendingNames.contains(msg)) offendingNames.add(msg);
            });
            error.addAllOffendingValues(offendingNames);
            return error;
        } else return null;
    }


    private List<ClinicalData> getCrfNotExistsOffenders(List<ClinicalData> data, Map<String, Set<CRFDefinition>> eventMap) {
        return data.stream().filter(clinicalData -> {
            Set<CRFDefinition> valid = eventMap.get(clinicalData.getEventName());
            if (valid == null) return false; // CRF Could not be verified is a separate class
            String crf = clinicalData.getCrfName();
            String version = clinicalData.getCrfVersion();
            List<CRFDefinition> matching = valid.stream()
                    .filter(crfDefinition -> crfDefinition.getName().equals(crf) && crfDefinition.getVersion().equals(version)).collect(Collectors.toList());
            return matching.size() == 0;
        }).collect(Collectors.toList());
    }


}
