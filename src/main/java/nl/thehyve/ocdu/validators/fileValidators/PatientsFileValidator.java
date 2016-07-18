package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.PatientDataFactory;
import nl.thehyve.ocdu.models.errors.FileFormatError;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

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
        try {
            String header = getHeader(file);
            List<String> allowed = Arrays.asList(PatientDataFactory.ALL_PERMITTED_COLUMNS);
            noOtherColumnsAllowed(header, allowed);
        } catch (IOException e) {
            setValid(false);
            addError(new FileFormatError("Internal Server Error prevented parsing the file. Contact administrator."));
            e.printStackTrace();
        }

    }

    private void noOtherColumnsAllowed(String header, List<String> allowed) {
        List<String> headerSplit = splitLine(header);
        for (String columnName: headerSplit) {
            if (!allowed.contains(columnName)) {
                setValid(false);
                addError(new FileFormatError("Column name not allowed: " + columnName));
            }
        }
    }
}
