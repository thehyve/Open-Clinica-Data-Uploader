package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.OcDefinitions.CRFDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public interface ClinicalDataCrossCheck {

    ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData);
     default Map<String, List<String>> buildEventMap(MetaData metaData) {
        Map<String, List<String>> eventMap = new HashMap<>();
        metaData.getEventDefinitions().stream().forEach(eventDefinition ->
                {
                    List<String> crfNames = eventDefinition.getCrfDefinitions()
                            .stream().map(CRFDefinition::getName)
                            .collect(Collectors.toList());
                    eventMap.put(eventDefinition.getName(), crfNames);
                }
        );
        return eventMap;
    }
}
