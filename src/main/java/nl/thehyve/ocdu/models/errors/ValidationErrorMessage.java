package nl.thehyve.ocdu.models.errors;

import nl.thehyve.ocdu.models.OCEntities.OcEntity;

import java.util.*;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */

public class ValidationErrorMessage {

    private String message;
    private List<String> offendingValues = new ArrayList<>();
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

    public void addAllOffendingValues(Collection<String> values) {offendingValues.addAll(values);}

    public Collection<String> getOffendingValues() {
        return offendingValues;
    }

    public static String generateOffendingValueString(OcEntity data, String value) {
        return "Value: " + value + " in: "+ data.toString();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationErrorMessage that = (ValidationErrorMessage) o;

        if (isError != that.isError) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return offendingValues.equals(that.offendingValues);

    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + offendingValues.hashCode();
        result = 31 * result + (isError ? 1 : 0);
        return result;
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
