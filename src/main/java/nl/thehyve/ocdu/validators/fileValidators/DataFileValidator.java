package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.errors.FileFormatError;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class DataFileValidator extends GenericFileValidator {

    public DataFileValidator() {
        super(ClinicalDataFactory.MANDATORY_HEADERS, new String[]{ClinicalDataFactory.EventRepeat});
    }

    @Override
    public void validateFile(Path file) {
        super.validateFile(file);
        /*try {
            //String header = getHeader(file);
            //columnNamesWellFormed(header);
        } catch (IOException e) {
            setValid(false);
            addError(new FileFormatError("Internal Server Error prevented parsing the file. Contact administrator."));
            e.printStackTrace();
        }*/
    }

    private void columnNamesWellFormed(String header) {
        // If item name contains "_" the part to the right of it must be a positive integer
        List<String> split = splitLine(header);
        split.stream().filter(columnName -> columnName.contains("_")).forEach(columnName -> {
            String[] itemSplit = columnName.split("_");
            int itemSplitLen = itemSplit.length;
            String lastToken = itemSplit[itemSplitLen - 1];
            if (!super.isInteger(lastToken)) {
                addError(new FileFormatError("Item name incorrect: " + columnName + "If column name contains _ part to the " +
                        "right of it must be an integer - to indicate group repeat."));
                setValid(false);
            } else {
                int intRep = Integer.parseInt(lastToken);
                if (intRep < 1) {
                    addError(new FileFormatError("Group repeat in item name: " + columnName + " incorrect. Group repeat must be greater than 0"));
                    setValid(false);
                }
            }
        });

    }

}
