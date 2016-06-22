package nl.thehyve.ocdu.validators;

import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.SiteDefinition;
import nl.thehyve.ocdu.models.errors.ValidationErrorMessage;
import nl.thehyve.ocdu.validators.clinicalDataChecks.SitesExistCrossCheck;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jacob Rousseau on 22-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class SitesExistCrossCheckTests {

    private List<ClinicalData> clinicalDataList;

    private MetaData metaData;

    @Test
    public void testSiteExistValidationAllSitesOK() {
        SiteDefinition siteDefinition = new SiteDefinition();
        siteDefinition.setName("Wrong site");
        metaData.getSiteDefinitions().add(siteDefinition);

        SitesExistCrossCheck sitesExistCrossCheck = new SitesExistCrossCheck();
        ValidationErrorMessage validationErrorMessage =
                sitesExistCrossCheck.getCorrespondingError(clinicalDataList, metaData, null, null, null, null);
        Assert.assertNull(validationErrorMessage);
    }

    @Test
    public void testSiteExistValidationNonExistingSite() {
        SitesExistCrossCheck sitesExistCrossCheck = new SitesExistCrossCheck();
        ValidationErrorMessage validationErrorMessage =
                sitesExistCrossCheck.getCorrespondingError(clinicalDataList, metaData, null, null, null, null);
        Assert.assertEquals("One or more of sites you used in your data file does not exist", validationErrorMessage.getMessage());
        Assert.assertEquals(1, validationErrorMessage.getOffendingValues().size());
        Assert.assertEquals("[Wrong site]", validationErrorMessage.getOffendingValues().toString());
    }

    @Before
    public void setUp() throws Exception {
        clinicalDataList = new ArrayList<>();
        //public ClinicalData(String study, String item, String ssid, String eventName, Integer eventRepeat, String crfName, UploadSession submission, String crfVersion, Integer groupRepeat, OcUser owner, String value) {
        ClinicalData data = new ClinicalData("HematologyStudySeven", "bloodpressure", "Subject_0001", "FUP", 1, "BaseLine-FUP", null, "0.5", 1, null, "80/120");
        data.setSite("Wrong site");
        clinicalDataList.add(data);

        data = new ClinicalData("HematologyStudySeven", "bloodpressure", "Subject_0002", "FUP", 1, "BaseLine-FUP", null, "0.5", 1, null, "80/120");
        data.setSite("Correct site");
        clinicalDataList.add(data);

        List<SiteDefinition> siteDefinitionList = new ArrayList<>();
        SiteDefinition siteDefinition = new SiteDefinition();
        siteDefinition.setName("Correct site");
        siteDefinitionList.add(siteDefinition);
        metaData = new MetaData();
        metaData.setSiteDefinitions(siteDefinitionList);
    }
}
