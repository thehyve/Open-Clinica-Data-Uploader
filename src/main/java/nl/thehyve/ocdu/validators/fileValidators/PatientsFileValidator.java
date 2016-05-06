package nl.thehyve.ocdu.validators.fileValidators;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class PatientsFileValidator extends GenericFileValidator{


    public PatientsFileValidator() {
        super(new String[]{}, new String[]{}); //TODO: put mandatory and integer columns here from PatientFactory
    }
}
