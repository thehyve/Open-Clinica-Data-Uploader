package nl.thehyve.ocdu.models;

import nl.thehyve.ocdu.models.OCEntities.OcEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */

public class ValidationErrorMessage {

    private String generalMessage;
    private List<String> offendingValues = new ArrayList<String>();
    private boolean isError = false; // Warning by default

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public ValidationErrorMessage(String generalMessage) {
        this.generalMessage = generalMessage;
    }

    public void addOffendingValue(String value) {
        offendingValues.add(value);
    }

    public List<String> getOffendingValues() {
        return offendingValues;
    }

    public static String generateOffendingValueString(OcEntity data, String value) {
        return "Value: " + value + " in: "+ data.toString();
    }
}
