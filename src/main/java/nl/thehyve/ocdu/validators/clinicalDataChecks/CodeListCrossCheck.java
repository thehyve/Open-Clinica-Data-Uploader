package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.CodeListDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.EnumerationError;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piotrzakrzewski on 17/05/16.
 */
public class CodeListCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        EnumerationError error = new EnumerationError();
        Map<ClinicalData, ItemDefinition> clinicalDataItemDefinitionMap = buildItemDefMap(data, metaData);
        Map<String, CodeListDefinition> codeListMap = new HashMap<>();
        metaData.getCodeListDefinitions().stream().forEach(codeListDefinition -> {
            codeListMap.put(codeListDefinition.getOcid(), codeListDefinition);
        });
        data.stream().forEach(clinicalData -> {
            List<String> values = clinicalData.getValues();
            ItemDefinition def = clinicalDataItemDefinitionMap.get(clinicalData);

            if (def != null) { // Non existent item is a separate error
                String codeListRef = def.getCodeListRef();
                if (codeListRef != null) {
                    CodeListDefinition codeListdef = codeListMap.get(codeListRef);
                    for (String value : values) {
                        if (codeListdef != null && !codeListdef.isAllowed(value)) {
                            String msg = clinicalData.getItem()+": "+value + " not in: " + codeListdef;
                            if( value.contains(" ")) msg += " (value contains whitespaces)";
                            else if(value.equals("")) msg+= " (value is an empty string)";
                            error.addOffendingValue(msg+ " for subject: "+ clinicalData.getSsid());
                        }
                    }
                }
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }
}
