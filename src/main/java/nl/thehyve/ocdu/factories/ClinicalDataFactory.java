package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.springframework.security.access.method.P;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class ClinicalDataFactory extends UserSubmittedDataFactory {


    public final static String STUDY_SUBJECT_ID = "StudySubjectID";
    public final static String STUDY = "Study";
    public final static String EventName = "EventName";
    public final static String EventRepeat = "EventRepeat";
    public final static String CRFName = "CRFName";
    public final static String CRFVersion = "CRFVersion";
    public final static String[] MANDATORY_HEADERS = {STUDY_SUBJECT_ID, STUDY, EventName, EventRepeat,
            CRFName, CRFVersion};

    public final static String SITE = "Site";

    public ClinicalDataFactory(OcUser user, UploadSession submission) {
        super(user, submission);
    }

    public List<ClinicalData> createClinicalData(Path dataFile) {
        try {
            Stream<String> lines = Files.lines(dataFile);
            Stream<String> lines2 = Files.lines(dataFile);
            Optional<String> headerLine = lines2.findFirst();
            HashMap<String, Integer> header = parseHeader(headerLine.get());
            HashMap<String, Integer> coreColumns = getCoreHeader(header);
            List<ClinicalData> clinicalData = new ArrayList<>();
            List<List<ClinicalData>> clinicalDataAggregates = lines.skip(1).
                    filter(s -> s.split(FILE_SEPARATOR).length > 2).
                    map(s -> parseLine(s, header, coreColumns)).collect(Collectors.toList());
            clinicalDataAggregates.forEach(aggregate -> clinicalData.addAll(aggregate));
            lines.close();
            lines2.close();
            return clinicalData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<String, Integer> getCoreHeader(HashMap<String, Integer> headerMap) {
        HashMap<String, Integer> coreMap = new HashMap<>();
        Integer ssidIndex = getAndRemove(headerMap, STUDY_SUBJECT_ID);
        coreMap.put(STUDY_SUBJECT_ID, ssidIndex);
        Integer studyIndex = getAndRemove(headerMap, STUDY);
        coreMap.put(STUDY, studyIndex);
        Integer eventNameIndex = getAndRemove(headerMap, EventName);
        coreMap.put(EventName, eventNameIndex);
        Integer eventRepeatIndex = getAndRemove(headerMap, EventRepeat);
        coreMap.put(EventRepeat, eventRepeatIndex);
        Integer crfNameIndex = getAndRemove(headerMap, CRFName);
        coreMap.put(CRFName, crfNameIndex);
        Integer crfVersionIndex = getAndRemove(headerMap, CRFVersion);
        coreMap.put(CRFVersion, crfVersionIndex);
        Integer siteIndex = getAndRemove(headerMap, SITE);
        coreMap.put(SITE, siteIndex);
        return coreMap;
    }

    private HashMap<String, Integer> parseHeader(String headerLine) {
        String[] split = headerLine.split(FILE_SEPARATOR);
        int i = 0;
        HashMap<String, Integer> header = new HashMap();
        List<String> tokens = Arrays.asList(split);
        for (String token : tokens) {
            header.put(token, i++);
        }
        return header;
    }

    private List<ClinicalData> parseLine(String line, HashMap<String,
            Integer> headerMap, HashMap<String, Integer> coreColumns) {
        String[] split = line.split(FILE_SEPARATOR);

        String ssid = split[coreColumns.get(STUDY_SUBJECT_ID)];
        String study = split[coreColumns.get(STUDY)];
        String eventName = split[coreColumns.get(EventName)];
        Integer eventRepeat = Integer.parseInt(split[coreColumns.get(EventRepeat)]);
        String crf = split[coreColumns.get(CRFName)];
        String crfVer = split[coreColumns.get(CRFVersion)];
        String site = split[coreColumns.get(SITE)];


        List<ClinicalData> aggregation = new ArrayList<>();
        for (String colName : headerMap.keySet()) {
            String item = parseItem(colName);
            Integer groupRepeat = parseGroupRepeat(colName);
            if (groupRepeat == null) item = colName; // Consequences of encoding group repeat in the column name

            String value = split[headerMap.get(colName)];
            ClinicalData dat = new ClinicalData(study, item, ssid, eventName, eventRepeat, crf,
                    getSubmission(), crfVer, groupRepeat, getUser(), value);
            dat.setSite(site);
            aggregation.add(dat);
        }
        return aggregation;
    }

    private String parseItem(String columnToken) {
        int last = columnToken.lastIndexOf("_");
        if (last > 0 ) {
            return columnToken.substring(0, last);
        } else {
            return columnToken;
        }
    }

    private Integer parseGroupRepeat(String columnToken) {
        String[] splt = columnToken.split("_");
        Integer gRep = null;
        if (splt.length > 1) {
            try {
                gRep = Integer.parseInt(splt[1]);
            } catch (NumberFormatException e) {
                gRep = null; // Do nothing
            }
        }
        return gRep;
    }

    private Integer getAndRemove(HashMap<String, Integer> map, String key) {
        Integer index = map.get(key);
        map.remove(key);
        return index;
    }

}
