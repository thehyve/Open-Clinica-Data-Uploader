package nl.thehyve.ocdu.validators.patientDataChecks;

import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.util.List;

/**
 * Created by bo on 6/7/16.
 */
public interface PatientDataCrossCheck {
    ValidationErrorMessage getCorrespondingError(List<Subject> data, MetaData metaData);

}
