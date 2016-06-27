package nl.thehyve.ocdu.nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OCEntities.Event;
import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.UploadSession;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class EventTests {

    private OcUser user;

    private Date now;

    private UploadSession uploadSession;


    @Test
    public void testCreateEventKey() {
        Event event = new Event();
        event.setStudy("TestStudy");
        event.setSite("SITE");
        event.setEventName("EventfulDay");
        event.setSsid("SUBJECT_0001");
        event.setLocation("Location");
        event.setRepeatNumber("6");

        assertEquals("TESTSTUDYSITESUBJECT_0001SE_EVENTFULDAY6", event.createEventKey("SE_EVENTFULDAY"));
    }


    @Before
    public void setUp() {
        user = new OcUser();
        now = GregorianCalendar.getInstance().getTime();
        uploadSession  = new UploadSession("sessionName", UploadSession.Step.EVENTS, now, user);
    }
}
