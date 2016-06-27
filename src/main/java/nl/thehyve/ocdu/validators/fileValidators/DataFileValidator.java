package nl.thehyve.ocdu.validators.fileValidators;

import nl.thehyve.ocdu.factories.ClinicalDataFactory;
import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.errors.FileFormatError;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by piotrzakrzewski on 11/04/16.
 */
public class DataFileValidator extends GenericFileValidator {

    private final List<Study> studies;
    private String crfSep = "-"; // - is a forbidden character in CRF and CRF version, it is safe to use as a separator

    public DataFileValidator(List<Study> studies) {
        super(ClinicalDataFactory.MANDATORY_HEADERS, new String[]{ClinicalDataFactory.EventRepeat});
        this.studies = studies;
    }

    @Override
    public void validateFile(Path file) {
        super.validateFile(file);
        try {
            String header = getHeader(file);
            String[] body = getBody(file);
            int columnIndex = getColumnIndex(header, ClinicalDataFactory.STUDY);
            if (columnIndex != -1) {
                Set<String> usedStudies = getUsedStudies(body, columnIndex);
                onlyOneStudy(usedStudies);
                studyExists(usedStudies);
            }
            if (getErrorMessages().size() > 0) {
                setValid(false);
            }
            //columnNamesWellFormed(header);
        } catch (IOException e) {
            setValid(false);
            addError(new FileFormatError("Internal Server Error prevented parsing the file. Contact administrator."));
            e.printStackTrace();
        }
    }

    private void studyExists(Set<String> usedStudies) {
        for (String studyName : usedStudies) {
            if (!studies.stream().anyMatch(study -> study.getName().equals(studyName))) {
                FileFormatError error = new FileFormatError("Study: " + studyName + " either does not exist or you have no access to it.");
                addError(error);
            }
        }
    }

    private void onlyOneStudy(Set<String> usedStudies) {
        if (usedStudies.size() != 1) {
            FileFormatError error = new FileFormatError("Data file must contain one and only one study");
            addError(error);
        }
    }

    private Set<String> getUsedStudies(String[] body, int columnIndex) {
        Set<String> usedStudies = new HashSet<>();
        for (int i = 0; i < body.length; i++) {
            String line = body[i];
            String[] split = line.split(ClinicalDataFactory.COLUMNS_DELIMITER);
            if (split.length > columnIndex) {
                String study = split[columnIndex];
                usedStudies.add(study);
            }
        }
        return usedStudies;
    }

    private void combinationOfCrfAndVersionExists(Set<String> usedCrfs) {

    }

    private void onlyOneCRF(Set<String> usedCrfs) {
        if (usedCrfs.size() != 1) {
            FileFormatError error = new FileFormatError("Data file must contain one and only one CRF and CRF version");
            addError(error);
        }
    }

    private Set<String> getUsedCRFs(String[] body, int crfColumnIndex, int versionColumnIndex) {
        Set<String> usedCRFs = new HashSet<>();
        for (int i = 0; i < body.length; i++) {
            String line = body[i];
            String[] split = line.split(ClinicalDataFactory.COLUMNS_DELIMITER);
            if (split.length > crfColumnIndex && split.length > versionColumnIndex) {
                String crfName = split[crfColumnIndex];
                String crfVersion = split[versionColumnIndex];
                usedCRFs.add(crfName + crfSep + crfVersion);
            }
        }
        return usedCRFs;
    }

}
