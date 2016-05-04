package nl.thehyve.ocdu.validators.fileValidators;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class PatientsFileValidator implements FileFormatValidator {

    private boolean valid;
    private List<String> humanreadableErrors;

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public List<String> getErrorMessages() {
        return this.humanreadableErrors;
    }

    @Override
    public void validateFile(Path file) {
        this.valid = true; //TODO: implement validation
        this.humanreadableErrors = new ArrayList<>();
    }
}
