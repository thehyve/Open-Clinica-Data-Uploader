package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 11/05/16.
 */
public class ItemDoesNotExist extends ValidationErrorMessage {
    public ItemDoesNotExist() {
        super("One or more items in your file do not exist in the OpenClinica CRF.");
    }
}
