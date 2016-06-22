package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.clinicalDataChecks.SiteSubjectMatchCrossCheck;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openclinica.ws.beans.SiteRefType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SiteSubjectMatchCrossCheckTests {

    private List<ClinicalData> clinicalDataList;

    @Test
    public void mismatchingSubjectIDSite() {
        List<StudySubjectWithEventsType> studySubjectWithEventsTypeList = new ArrayList<>();
        StudySubjectWithEventsType subjectWithEventsType = new StudySubjectWithEventsType();
        subjectWithEventsType.setLabel("Subject_0001");
        StudyRefType studyRefType = new StudyRefType();
        studyRefType.setIdentifier("HematologyStudyForty");
        SiteRefType siteRefType = new SiteRefType();
        // Subject_0001 is associated with the site "Wrong site" in the clinicalData created below.
        siteRefType.setIdentifier("Correct site");
        studyRefType.setSiteRef(siteRefType);
        subjectWithEventsType.setStudyRef(studyRefType);

        studySubjectWithEventsTypeList.add(subjectWithEventsType);

        SiteSubjectMatchCrossCheck siteSubjectMatchCrossCheck = new SiteSubjectMatchCrossCheck();

        ValidationErrorMessage validationErrorMessage =
                siteSubjectMatchCrossCheck.getCorrespondingError(clinicalDataList, null, null, studySubjectWithEventsTypeList, null, null);

        Assert.assertEquals("One or more existing subjects have a mismatching site specified in your data file", validationErrorMessage.getMessage());
        Assert.assertEquals(1, validationErrorMessage.getOffendingValues().size());
        Assert.assertEquals("[Subject_0001]", validationErrorMessage.getOffendingValues().toString());

        // now the correct happy flow
        clinicalDataList.get(0).setSite("Correct site");
        validationErrorMessage =
                siteSubjectMatchCrossCheck.getCorrespondingError(clinicalDataList, null, null, studySubjectWithEventsTypeList, null, null);

        Assert.assertNull(validationErrorMessage);
    }

    @Before
    public void setUp() throws Exception {
        clinicalDataList = new ArrayList<>();
        //public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, UploadSession submission, String crfVersion, Integer groupRepeat, OcUser owner, String value) {
        ClinicalData data = new ClinicalData("HematologyStudyForty", "bloodpressure", "Subject_0001", "FUP", 1, "BaseLine-FUP", null, "0.5", 1, null, "80/120");
        data.setSite("Wrong site");
        clinicalDataList.add(data);

        data = new ClinicalData("HematologyStudyForty", "bloodpressure", "Subject_0002", "FUP", 1, "BaseLine-FUP", null, "0.5", 1, null, "80/120");
        data.setSite("Correct site");
        clinicalDataList.add(data);

        List<SiteDefinition> siteDefinitionList = new ArrayList<>();
        SiteDefinition siteDefinition = new SiteDefinition();
        siteDefinition.setName("Correct site");
        siteDefinitionList.add(siteDefinition);
    }
}
