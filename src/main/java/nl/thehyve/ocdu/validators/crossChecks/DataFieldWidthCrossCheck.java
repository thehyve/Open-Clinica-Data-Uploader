package nl.thehyve.ocdu.validators.crossChecks;

import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class DataFieldWidthCrossCheck implements ClinicalDataCrossCheck {
    public static final int SSID_MAX_LENGTH = 30; //TODO: make configurable

    @Override
    public ValidationErrorMessage getCorrespondingError(List<ClinicalData> data, MetaData metaData) {
        List<String> violatingSSIDs = new ArrayList<>();
        data.stream()
                .filter(ocEntity -> ocEntity.getSsid().length() > SSID_MAX_LENGTH)
                .forEach(ocEntity -> {
                            if (!violatingSSIDs.contains(ocEntity.getSsid())) {
                                violatingSSIDs.add(ocEntity.getSsid());
                            }
                        }
                );
        if (violatingSSIDs.size() > 0) {
            ValidationErrorMessage error = new ValidationErrorMessage("One or more of the " +
                    "SSIDs you defined is too long. Max length: " + SSID_MAX_LENGTH);
            error.addAllOffendingValues(violatingSSIDs);
            return error;
        } else return null;
    }
}
