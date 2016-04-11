package nl.thehyve.ocdu.validators;

import java.io.File;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class DataFileValidator implements FileValidator {

    @Override
    public boolean isValid(File file) {
        return false;
    }
}
