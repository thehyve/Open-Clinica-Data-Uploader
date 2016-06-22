package nl.thehyve.ocdu.models.errors;

/**
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SiteDoesNotExist extends ValidationErrorMessage {

    public SiteDoesNotExist() {
        super("One or more of sites you used in your data file does not exist");
    }
}
