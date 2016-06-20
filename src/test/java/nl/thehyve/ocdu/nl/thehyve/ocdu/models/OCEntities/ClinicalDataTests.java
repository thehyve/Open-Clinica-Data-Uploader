package nl.thehyve.ocdu.nl.thehyve.ocdu.models.OCEntities;

import junit.framework.Assert;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class ClinicalDataTests {

    private OcUser user;

    private Date now;

    private UploadSession uploadSession;


    @Test
    public void testCreateEventKey() {
        ClinicalData clinicalData = new ClinicalData("TestStudy", "Bloodpressure",
                "SUBJECT_0001", "EventfulDay", 1, "CRF-Baseline", uploadSession, "v10.3", 3, user, "80/120");
        assertEquals("SUBJECT_0001EVENTFULDAY1", clinicalData.createEventKey());
    }


    @Before
    public void setUp() {
        user = new OcUser();
        now = GregorianCalendar.getInstance().getTime();
        uploadSession  = new UploadSession("sessionName", UploadSession.Step.EVENTS, now, user);
    }
}
