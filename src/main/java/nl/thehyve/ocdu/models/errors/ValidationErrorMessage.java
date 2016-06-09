package nl.thehyve.ocdu.models.errors;

import nl.thehyve.ocdu.models.OCEntities.OcEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */

public class ValidationErrorMessage {

    private String message;
    private List<String> offendingValues = new ArrayList<String>();
    private boolean isError = true; // Error by default

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public ValidationErrorMessage(String message) {
        this.message = message;
    }

    public void addOffendingValue(String value) {
        offendingValues.add(value);
    }

    public void addAllOffendingValues(List<String> values) {offendingValues.addAll(values);}

    public List<String> getOffendingValues() {
        return offendingValues;
    }

    public static String generateOffendingValueString(OcEntity data, String value) {
        return "Value: " + value + " in: "+ data.toString();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ValidationErrorMessage{" +
                "message='" + message + '\'' +
                ", offendingValues=" + offendingValues +
                ", isError=" + isError +
                '}';
    }
}
