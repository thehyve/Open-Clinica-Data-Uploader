package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import nl.thehyve.ocdu.models.OCEntities.Subject;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by bo on 6/6/16.
 */
public class PatientDataFactory extends UserSubmittedDataFactory {

    public final static String STUDY_SUBJECT_ID = "StudySubjectID";
    public final static String GENDER = "Gender";
    public final static String BIRTH = "Date of Birth";
    public final static String PERSON_ID = "Person ID";
    public final static String SECONDARY_ID = "Secondary ID";
    public final static String STUDY = "Study";
    public final static String SITE = "Site";
    public final static String[] MANDATORY_HEADERS = {STUDY_SUBJECT_ID, STUDY, SITE};

    public PatientDataFactory(OcUser user, UploadSession submission) {
        super(user, submission);
    }

    public List<Subject> createPatientData(Path patientFile) {
//        try {
//            Stream<String> lines = Files.lines(patientFile);
//            Stream<String> lines2 = Files.lines(patientFile);
//            Optional<String> headerLine = lines2.findFirst();
//            HashMap<String, Integer> header = parseHeader(headerLine.get());
//            HashMap<String, Integer> coreColumns = getCoreHeader(header);
//            List<ClinicalData> clinicalData = new ArrayList<>();
//            List<List<ClinicalData>> clinicalDataAggregates = lines.skip(1). // skip header
//                    filter(s -> s.split(FILE_SEPARATOR).length > 2). // smallest legal file consists of no less than 3 columns
//                    map(s -> parseLine(s, header, coreColumns)).collect(Collectors.toList());
//            clinicalDataAggregates.forEach(aggregate -> clinicalData.addAll(aggregate));
//            lines.close();
//            lines2.close();
//            return clinicalData;
//        }

        return null;
    }
}
