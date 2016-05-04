package nl.thehyve.ocdu.validators.checks;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.MetaData;
import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.ValidationErrorMessage;

import static nl.thehyve.ocdu.models.ValidationErrorMessage.generateOffendingValueString;

/**
 * Created by piotrzakrzewski on 04/05/16.
 */
public class DataFieldWidthCheck implements OcEntityCheck {

    public static final int ITEM_MAX_LENGTH = 4000; //TODO: make configurable
    public static final int SSID_MAX_LENGTH = 30; //TODO: make configurable

    @Override
    public ValidationErrorMessage getCorrespondingError(OcEntity dataRecord, MetaData metaData) {
        ClinicalData data = (ClinicalData) dataRecord;
        ValidationErrorMessage error = new ValidationErrorMessage("One or more values in your " +
                "data violate Open Clinica field width constraints");
        String item = data.getItem();
        String ssid = data.getSsid();
        boolean itemLengthViolated = item.length() > ITEM_MAX_LENGTH;
        boolean ssidLengthViolated = ssid.length() > SSID_MAX_LENGTH;
        if (itemLengthViolated ) {
            error.addOffendingValue(generateOffendingValueString(data, item));
        }
        if (ssidLengthViolated) {
            error.addOffendingValue(generateOffendingValueString(data, ssid));
        }
        if (itemLengthViolated || ssidLengthViolated) {
            return error;
        } else return null;
    }

}
