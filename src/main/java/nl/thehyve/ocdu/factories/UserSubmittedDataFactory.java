package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class UserSubmittedDataFactory {

    public final static String COLUMNS_DELIMITER = "\t";

    private final UploadSession submission;
    private final OcUser user;

    public UserSubmittedDataFactory(OcUser user, UploadSession submission) {
        this.user = user;
        this.submission = submission;
    }

    public UploadSession getSubmission() {
        return submission;
    }

    public OcUser getUser() {
        return user;
    }

    protected static Optional<String[]> getHeaderRow(Path tabularFilePath) {
        try (Stream<String> lines = Files.lines(tabularFilePath)) {
            return lines.findFirst().map(UserSubmittedDataFactory::parseLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static Map<String, Integer> createColumnsIndexMap(String[] headerRow) {
        HashMap<String, Integer> result = new HashMap<>(headerRow.length);
        for (int i = 0; i < headerRow.length; i++) {
            if (result.containsKey(headerRow[i])) {
                throw new RuntimeException("Name" + headerRow[i] + " appears more than once in the header: " + headerRow.toString());
            }
            result.put(headerRow[i], i);
        }
        return result;
    }

    protected static String[] parseLine(String line) {
        return line.split(COLUMNS_DELIMITER);
    }

}
