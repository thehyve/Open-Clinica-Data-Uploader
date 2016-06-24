package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.PatientDataFactory;

import java.nio.file.Path;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class PatientsFileValidator extends GenericFileValidator{


    public PatientsFileValidator() {
        super(PatientDataFactory.MANDATORY_HEADERS, new String[]{});
    }

    @Override
    public void validateFile(Path file) {
        super.validateFile(file);
    }
}
