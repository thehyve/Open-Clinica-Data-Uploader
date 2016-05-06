package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.errors.FileFormatError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 06/05/16.
 */
public  class GenericFileValidator implements FileFormatValidator {


    public GenericFileValidator(String[] mandatoryColumns, String[] positiveIntegerColumns) {
        this.mandatoryHeaders = mandatoryColumns;
        this.positiveIntegerColumns = positiveIntegerColumns;
    }


    private String[] mandatoryHeaders;
    private String[] positiveIntegerColumns;
    private boolean valid = true;
    private List<FileFormatError> errors = new ArrayList<>();

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public List<FileFormatError> getErrorMessages() {
        return this.errors;
    }

    protected String getHeader(Path file) throws IOException {
        Stream<String> headerStream = Files.lines(file);
        String header = headerStream.findFirst().get();
        headerStream.close();
        return header;
    }

    protected String[] getBody(Path file) throws IOException {
        Stream<String> bodyStream = Files.lines(file);
        String[] body =  bodyStream.skip(1).toArray(size -> new String[size]);
        bodyStream.close();
        return body;
    }

    @Override
    public void validateFile(Path file) {
        errors = new ArrayList<>();
        valid = true;
        try {
            String[] body = getBody(file);
            String header = getHeader(file);

            mandatoryHeaders(header);
            allRowsSameLength(body, header);//TODO:Refactor: Performance. Right now we iterate through lines twice, can be done using streams with just one
            for (String intHeader : positiveIntegerColumns) {
                int index = getColumnIndex(header, intHeader);
                columnPositiveInteger(body, index);
            }

        } catch (IOException e) {
            this.valid = false;
            errors.add(new FileFormatError("Internal Server Error prevented parsing the file. Contact administrator."));
            e.printStackTrace();
        }
    }

    protected int getColumnIndex(String header, String column) {
        List<String> split = splitLine(header);
        return split.indexOf(column);
    }

    private void columnPositiveInteger(String[] body, int index) {
        if (index == -1) return;
        for (String line : body) {
            List<String> split = splitLine(line);
            String field = split.get(index);
            if (!isInteger(field)) {
                errors.add(new FileFormatError("Repeat number must be an integer, offending value: " + field));
                valid = false;
            } else {
                int intRep = Integer.parseInt(field);
                if (intRep < 1) {
                    errors.add(new FileFormatError("Repeat numbering starts with 1, offending value: " + field));
                    valid = false;
                }
            }
        }
    }

    private void allRowsSameLength(String[] body, String header) {
        int expectedLength = splitLine(header).size();
        for (String line : body) {
            List<String> split = splitLine(line);
            if (split.size() != expectedLength && split.size() != 0) {  // empty lines are fine
                errors.add(new FileFormatError("Following line has different number of fields than the header:" + line));
                valid = false;
            }
        }
    }



    protected boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mandatoryHeaders(String headerLine) {
        List<String> split = splitLine(headerLine);
        //TODO:refactor: Move FILE_SEPARATOR to a separate config class
        for (String mandatoryHeader : mandatoryHeaders) {
            if (!split.contains(mandatoryHeader)) {
                errors.add(new FileFormatError("Column missing: " + mandatoryHeader));
                valid = false;
            }
        }
    }

    protected List<String> splitLine(String line) {
        return Arrays.asList(line.split(ClinicalDataFactory.FILE_SEPARATOR));
    }

    protected void setValid(boolean newValidValue) {
        this.valid = newValidValue;
    }

    protected void addError(FileFormatError newError) {
        this.errors.add(newError);
    }

}
