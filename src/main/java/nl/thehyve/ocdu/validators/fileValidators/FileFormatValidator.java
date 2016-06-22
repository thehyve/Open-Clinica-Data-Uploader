package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.models.errors.FileFormatError;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public interface FileFormatValidator {

    boolean isValid();

    Collection<ValidationErrorMessage> getErrorMessages();

    void validateFile(Path file);
}
