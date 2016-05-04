package nl.thehyve.ocdu.validators.fileValidators;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public interface FileFormatValidator {

    boolean isValid();

    List<String> getErrorMessages();

    void validateFile(Path file);
}
