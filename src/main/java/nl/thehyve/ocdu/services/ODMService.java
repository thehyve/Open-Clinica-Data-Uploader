package nl.thehyve.ocdu.services;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.soap.SOAPRequestFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by jacob on 6/8/16.
 */
@Service
public class ODMService {

    private static final String STUDY_SUBJECT_PARAM = "${SUBJECT_OID}";

    private static final String STUDY_EVENT_DATA_PARAM = "${STUDY_EVENT_DATA}";

    private static final String STUDY_EVENT_REPEAT_ORDINAL_PARAM = "${STUDY_EVENT_REPEAT_ORDINAL}";

    private static final String CRF_OID_PARAM = "${CRF_OID}";

    private static final String CRF_VERSION_PARAM = "${CRF_VERSION}";

    private static final String ITEM_GROUP_PARAM = "${ITEM_GROUP_PARAM}";

    private static final String ITEM_DATA_PARAM = "${ITEM_DATA_PARAM}";

    private static final String ITEM_OID_PARAM = "${ITEM_OID_PARAM}";

    private static final String ITEM_VALUE_PARAM = "${ITEM_VALUE_PARAM}";

    private static final String STATUS_AFTER_UPLOAD_PARAM = "${STATUS_AFTER_UPLOAD_PARAM}";

    // TODO: add the post-upload CRF status to the template
    /**
     * Template for the subjects section in an ODM-file
     */
    private static final String ODM_SUBJECT_SECTION =
            "<SubjectData SubjectKey=\"" + STUDY_SUBJECT_PARAM + "\">"
          + "<StudyEventData StudyEventOID=\""+ STUDY_EVENT_DATA_PARAM + "\" StudyEventRepeatKey=\"" + STUDY_EVENT_REPEAT_ORDINAL_PARAM + "\">"
          + "<FormData FormOID=\"" + CRF_OID_PARAM + "\" OpenClinica:Status=\"" + STATUS_AFTER_UPLOAD_PARAM + "\">"
          + "<ItemGroupData ItemGroupOID=\"" + ITEM_GROUP_PARAM + "\" TransactionType=\"Insert\" >"
          + ITEM_DATA_PARAM
          + "</ItemGroupData>"
          + "</FormData>"
          + "</StudyEventData>"
          + "</SubjectData>";

    /**
     * Template for the section of the individual items.
     */
    private static final String ODM_ITEM_SECTION =
            "<ItemData ItemOID=\"" + ITEM_OID_PARAM + "\" Value=\"" + ITEM_VALUE_PARAM + "\"/>";

    private static final Logger log = LoggerFactory.getLogger(ODMService.class);

    public String generateODM(List<ClinicalData> clinicalDataList,
                              MetaData metaData,
                              String statusAfterUpload,
                              Map<String, String> subjectLabelToOIDMap) throws Exception {
        StringBuffer odmDocument =
                buildODM(clinicalDataList, metaData.getStudyOID(), statusAfterUpload, metaData, subjectLabelToOIDMap);
        return odmDocument.toString();
    }


    private void addODMDocumentHeader(String studyOID, StringBuffer odmData) throws Exception {

        odmData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        odmData.append("<ODM xmlns=\"http://www.cdisc.org/ns/odm/v1.3\" ");
        odmData.append("xsi:schemaLocation=\"http://www.cdisc.org/ns/odm/v1.3 OpenClinica-ODM1-3-0-OC2-0.xsd\" ");
        odmData.append("xmlns:OpenClinica=\"http://www.openclinica.org/ns/odm_ext_v130/v3.1\" ");
        odmData.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        odmData.append("ODMVersion=\"1.3\" ");
        odmData.append("FileOID=\"");
        odmData.append(System.currentTimeMillis() + "");
        odmData.append("\" ");
        odmData.append("FileType=\"Snapshot\" ");
        odmData.append("Description=\"Dataset ODM\" ");
        odmData.append("CreationDateTime=\"");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        String dateTimeStamp = df.format(GregorianCalendar.getInstance().getTime());
        odmData.append(dateTimeStamp);
        odmData.append("\">");
        odmData.append("<ClinicalData StudyOID=\"");
        odmData.append(studyOID);
        odmData.append("\" ");
        odmData.append("MetaDataVersionOID=\"v1.0.0\">");
        odmData.append("<UpsertOn NotStarted=\"true\" DataEntryStarted=\"true\" DataEntryComplete=\"true\"/>");
    }

    private void addClosingTags(StringBuffer odmData) {
        odmData.append("</ClinicalData>");
        odmData.append("</ODM>");
    }

    private void appendSubjectODMSection(StringBuffer odmData,
                                         List<ClinicalData> clinicalDataList,
                                         String statusAfterUpload,
                                         Map<String, String> eventNameOIDMap,
                                         Map<String, String> subjectLabelToOIDMap) {
        // should not be possible but we just to be sure
        if (clinicalDataList.size() == 0) {
            return;
        }
        String subjectLabel = clinicalDataList.get(0).getSsid();
        String subjectOID = subjectLabelToOIDMap.get(subjectLabel);
        if (StringUtils.isEmpty(subjectOID)) {
            throw new IllegalStateException("No subjectOID present in OpenClinica for subject with label: '" + subjectLabel + "'");
        }
        String eventName = eventNameOIDMap.get(clinicalDataList.get(0).getEventName());
        Integer eventRepeatOrdinal = clinicalDataList.get(0).getEventRepeat();
        String crfName = clinicalDataList.get(0).getCrfName();
        String crfVersion = clinicalDataList.get(0).getCrfVersion();

        StrBuilder builder = new StrBuilder(ODM_SUBJECT_SECTION);
        builder.replaceAll(STUDY_SUBJECT_PARAM, subjectOID);
        builder.replaceAll(STUDY_EVENT_DATA_PARAM, eventName);
        builder.replaceAll(STUDY_EVENT_REPEAT_ORDINAL_PARAM, eventRepeatOrdinal.toString());
        builder.replaceAll(CRF_OID_PARAM, crfName);
        builder.replaceAll(STATUS_AFTER_UPLOAD_PARAM, statusAfterUpload);
        StrBuilder itemDataBuilder = new StrBuilder();
        for (ClinicalData clinicalData : clinicalDataList) {

            StrBuilder itemBuilder = new StrBuilder(ODM_ITEM_SECTION);
            itemBuilder.replaceAll(ITEM_OID_PARAM, StringEscapeUtils.escapeXml(clinicalData.getItem()));
            itemBuilder.replaceAll(ITEM_VALUE_PARAM, StringEscapeUtils.escapeXml(clinicalData.getValue()));
            itemDataBuilder.append(itemBuilder);

        }
        builder.replaceAll(ITEM_DATA_PARAM, itemDataBuilder.toString());

        odmData.append(builder.toStringBuffer());
    }

    private StringBuffer buildODM(List<ClinicalData> clinicalDataList, String studyOID, String statusAfterUpload, MetaData metaData, Map<String, String> subjectLabelToOIDMap) throws Exception {
        long startTime = System.currentTimeMillis();

        StringBuffer odmData = new StringBuffer("");
        if (clinicalDataList.size() == 0) {
            return odmData;
        }
        addODMDocumentHeader(studyOID, odmData);

        Map<String, String> eventNameOIDMap =
                metaData.getEventDefinitions().stream().collect(Collectors.toMap(EventDefinition::getName, EventDefinition::getStudyEventOID));

        Map<String, List<ClinicalData>> outputMap = clinicalDataList.stream().collect(Collectors.groupingBy(ClinicalData::createODMKey,
                Collectors.toList()));

        TreeMap<String, List<ClinicalData>> sortedMap = new TreeMap<>(outputMap);
        for (String key : sortedMap.keySet()) {
            List<ClinicalData> outputClinicalData = sortedMap.get(key);
            appendSubjectODMSection(odmData, outputClinicalData, statusAfterUpload, eventNameOIDMap, subjectLabelToOIDMap);
        }

        addClosingTags(odmData);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Finished ODM generation for study " + studyOID + " in " + duration + " milliseconds for " + clinicalDataList.size() + " data points");

        return odmData;
    }
}
