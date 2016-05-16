package nl.thehyve.ocdu.models.errors;

import javax.validation.Valid;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
public class RangeCheckViolation extends ValidationErrorMessage {
    public RangeCheckViolation() {
        super("One or more values in your data violates value range constraints (RangeChecks");
    }
}
