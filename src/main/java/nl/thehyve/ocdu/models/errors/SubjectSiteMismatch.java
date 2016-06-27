package nl.thehyve.ocdu.models.errors;

/**
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SubjectSiteMismatch extends ValidationErrorMessage {

    public SubjectSiteMismatch() {
        super("One or more existing subjects have a mismatching site specified in your data file");
    }
}
