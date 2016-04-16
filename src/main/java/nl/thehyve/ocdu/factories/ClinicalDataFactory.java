package nl.thehyve.ocdu.factories;

import nl.thehyve.ocdu.models.ClinicalData;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by piotrzakrzewski on 16/04/16.
 */
public class ClinicalDataFactory extends UserSubmittedDataFactory {


    public final static String STUDY_SUBJECT_ID = "StudySubjectID";
    public final static String STUDY_SITE = "StudySite";
    public final static String EventName = "EventName";
    public final static String EventRepeat = "EventRepeat";
    public final static String CRFName = "CRFName";
    public final static String CRFVersion = "CRFVersion";

    public ClinicalDataFactory(String userName, String submission) {
        super(userName, submission);
    }

    public List<ClinicalData> createClinicalData(Path dataFile) {
        try {
            Stream<String> lines = Files.lines(dataFile);
            Stream<String> lines2 = Files.lines(dataFile);
            Optional<String> headerLine = lines2.findFirst();
            HashMap<String, Integer> header = parseHeader(headerLine.get());
            List<ClinicalData> clinicalData = new ArrayList<>();
            List<List<ClinicalData>> clinicalDataAggregates = lines.skip(1).
                    filter(s -> s.split(FILE_SEPARATOR).length > 2).
                    map(s -> parseLine(s, header)).collect(Collectors.toList());
            clinicalDataAggregates.forEach(aggregate -> clinicalData.addAll(aggregate));
            lines.close();
            lines2.close();
            return clinicalData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private List<ClinicalData> parseLine(String line, HashMap<String, Integer> headerMapOriginal) {
        String[] split = line.split(FILE_SEPARATOR);
        HashMap<String, Integer> headerMap = (HashMap<String, Integer>) headerMapOriginal.clone();
        Integer ssidIndex = getAndRemove(headerMap, STUDY_SUBJECT_ID);
        Integer studyIndex = getAndRemove(headerMap, STUDY_SITE);
        Integer eventNameIndex = getAndRemove(headerMap, EventName);
        Integer eventRepeatIndex = getAndRemove(headerMap, EventRepeat);
        Integer crfNameIndex = getAndRemove(headerMap, CRFName);
        Integer crfVersionIndex = getAndRemove(headerMap, CRFVersion);

        String ssid = split[ssidIndex];
        String study = split[studyIndex];
        String eventName = split[eventNameIndex];
        Integer eventRepeat = Integer.parseInt(split[eventRepeatIndex]);
        String crf = split[crfNameIndex];
        String crfVer = split[crfVersionIndex];


        List<ClinicalData> aggregation = new ArrayList<>();
        for (String colName : headerMap.keySet()) {
            String item = parseItem(colName);
            Integer groupRepeat = parseGroupRepeat(colName);
            String value = split[headerMap.get(colName)];
            ClinicalData dat = new ClinicalData(study, item, ssid, eventName, eventRepeat, crf,
                    getSubmission(), crfVer, groupRepeat, getUserName(), value);
            aggregation.add(dat);
        }
        return aggregation;
    }

    private String parseItem(String columnToken) {
        String[] splt = columnToken.split("_");
        return splt[0];
    }

    private Integer parseGroupRepeat(String columnToken) {
        String[] splt = columnToken.split("_");
        Integer gRep = null;
        if (splt.length > 1) {
            gRep = Integer.parseInt(splt[1]);
        }
        return gRep;
    }

    private Integer getAndRemove(HashMap<String, Integer> map, String key) {
        Integer index = map.get(key);
        map.remove(key);
        return index;
    }

}
