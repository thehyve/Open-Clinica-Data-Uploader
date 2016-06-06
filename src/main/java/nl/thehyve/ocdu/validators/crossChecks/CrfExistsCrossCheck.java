package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.errors.CRFDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class CrfExistsCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        Map<String, List<CRFDefinition>> eventMap = buildEventMap(metaData);
        List<ClinicalData> crfNotExistsOffenders = getCrfNotExistsOffenders(data, eventMap);

        if (crfNotExistsOffenders.size() > 0) {
            CRFDoesNotExist error = new CRFDoesNotExist();
            List<String> offendingNames = new ArrayList<>();
            crfNotExistsOffenders.stream().forEach(clinicalData -> {
                String crf = clinicalData.getCrfName();
                String eventName = clinicalData.getEventName();
                String version = clinicalData.getCrfVersion();
                String msg = "CRF: " + crf + " version: " + version + " in event: " + eventName+ " for subject: "
                        + clinicalData.getSsid();
                if (!offendingNames.contains(msg)) offendingNames.add(msg);
            });
            error.addAllOffendingValues(offendingNames);
            return error;
        } else return null;
    }


    private List<ClinicalData> getCrfNotExistsOffenders(List<ClinicalData> data, Map<String, List<CRFDefinition>> eventMap) {
        return data.stream().filter(clinicalData -> {
            List<CRFDefinition> valid = eventMap.get(clinicalData.getEventName());
            if (valid == null) return false; // CRF Could not be verified is a separate class
            String crf = clinicalData.getCrfName();
            String version = clinicalData.getCrfVersion();
            List<CRFDefinition> matching = valid.stream()
                    .filter(crfDefinition -> crfDefinition.getName().equals(crf) && crfDefinition.getVersion().equals(version)).collect(Collectors.toList());
            return matching.size() == 0;
        }).collect(Collectors.toList());
    }


}
