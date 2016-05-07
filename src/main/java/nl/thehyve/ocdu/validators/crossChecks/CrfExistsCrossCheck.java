package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.errors.CRFDoesNotExist;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class CrfExistsCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        Map<String, List<String>> eventMap = buildEventMap(metaData);
        List<ClinicalData> offenders = data.stream().filter(clinicalData -> {
            List<String> valid = eventMap.get(clinicalData.getEventName());
            String crf = clinicalData.getCrfName();
            if (valid == null || !valid.contains(crf))
                return true;
            else return false;
        }).collect(Collectors.toList());
        if (offenders.size() > 0) {
            CRFDoesNotExist error = new CRFDoesNotExist("One or more CRFs you used in your data file is not present in the referenced event");
            List<String> offendingNames = new ArrayList<>();
            offenders.stream().forEach(clinicalData -> {
                String crf = clinicalData.getCrfName();
                if (!offendingNames.contains(crf)) offendingNames.add(crf);
            });
            error.addAllOffendingValues(offendingNames);
            return error;
        } else return null;
    }

    private Map<String, List<String>> buildEventMap(MetaData metaData) {
        Map<String, List<String>> eventMap = new HashMap<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition ->
                {
                    List<String> crfNames = new ArrayList<String>();
                    for (CRFDefinition crf : eventDefinition.getCrfDefinitions()) {
                        crfNames.add(crf.getOid());
                    }
                    eventMap.put(eventDefinition.getStudyEventOID(), crfNames);
                }
        );
        return eventMap;
    }
}
