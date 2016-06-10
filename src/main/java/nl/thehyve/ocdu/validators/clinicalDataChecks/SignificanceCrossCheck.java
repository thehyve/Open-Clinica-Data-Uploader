package nl.thehyve.ocdu.validators.clinicalDataChecks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.TooManySignificantDigits;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.List;

/**
 * Created by piotrzakrzewski on 16/05/16.
 */
public class SignificanceCrossCheck implements ClinicalDataCrossCheck {
    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData, List<StudySubjectWithEventsType> subjectWithEventsTypeList) {
        TooManySignificantDigits error = new TooManySignificantDigits();
        data.forEach(clinicalData -> {
            ItemDefinition definition = getMatching(clinicalData, metaData);
            if (definition != null) {
                addOffendingValues(error, clinicalData, definition);
            }
        });
        if (error.getOffendingValues().size() > 0) {
            return error;
        } else
            return null;
    }

    private void addOffendingValues(TooManySignificantDigits error, ClinicalData clinicalData, ItemDefinition definition) {
        for (String value : clinicalData.getValues()) {
            int digitsAfterDM = getDigitsAfterDM(value);
            if (digitsAfterDM > definition.getSignificantDigits()) {
                error.addOffendingValue("Item: " + clinicalData.getItem() +
                        " value: " + value + " expected number of significant digits: "
                        + definition.getSignificantDigits()+ " for subject: "+ clinicalData.getSsid() ) ;
            }
        }
    }

    private int getDigitsAfterDM(String value) {
        if (!isFloat(value)) {
            return 0;
        } else {
            String[] split = value.split("\\.");
            if (split.length != 2) {
                return 0;
            } else {
                String afterDM = split[1];
                return afterDM.length();
            }
        }
    }
}
