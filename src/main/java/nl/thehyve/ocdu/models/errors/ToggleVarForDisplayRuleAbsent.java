package nl.thehyve.ocdu.models.errors;

/**
 * Created by piotrzakrzewski on 15/06/16.
 */
public class ToggleVarForDisplayRuleAbsent extends ValidationErrorMessage {
    public ToggleVarForDisplayRuleAbsent() {
        super("One or more items in your submission has associated Display Rule which requires other item for validation but the item is absent.");
    }
}
