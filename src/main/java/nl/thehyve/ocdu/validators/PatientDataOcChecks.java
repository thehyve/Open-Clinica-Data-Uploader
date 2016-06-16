package nl.thehyve.ocdu.validators;


import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.patientDataChecks.PatientDataCheck;
import nl.thehyve.ocdu.validators.patientDataChecks.GenderPatientDataCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bo on 6/7/16.
 */
public class PatientDataOcChecks {

    private final List<Subject> subjects;
    private final MetaData metadata;

    private List<PatientDataCheck> checks = new ArrayList<>();

    public PatientDataOcChecks(MetaData metadata, List<Subject> subjects) {
        this.metadata = metadata;
        this.subjects = subjects;
        checks.add(new GenderPatientDataCheck());
    }

    public List<ValidationErrorMessage> getErrors() {
        List<ValidationErrorMessage> errors = new ArrayList<>();
        int index = 1;
        for (Subject subject : subjects) {
            for (PatientDataCheck check : checks) {
                ValidationErrorMessage error = check.getCorrespondingError(index, subject, metadata);
                if (error != null) {
                    errors.add(error);
                }
            }
            index++;
        }
        return errors;

    }

}
