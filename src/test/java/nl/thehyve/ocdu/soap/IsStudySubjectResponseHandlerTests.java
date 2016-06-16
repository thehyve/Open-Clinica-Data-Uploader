package nl.thehyve.ocdu.soap;

import nl.thehyve.ocdu.soap.ResponseHandlers.IsStudySubjectResponseHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Jacob Rousseau on 15-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class IsStudySubjectResponseHandlerTests {

    static SOAPMessage subjectPresentResponse;

    static SOAPMessage subjectAbsentResponse;


    @Test
    public void testIsStudySubjectForExistingSubject() throws Exception {
        String subjectOID = IsStudySubjectResponseHandler.parseIsStudySubjectResponse(subjectPresentResponse);
        assertEquals("SS_EV00001", subjectOID);
    }

    @Test
    public void testIsStudySubjectForNonExistingSubject() throws Exception {
        String subjectOID = IsStudySubjectResponseHandler.parseIsStudySubjectResponse(subjectAbsentResponse);
        assertEquals(null, subjectOID);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        Path isStudySubjectExistingSubject = Paths.get("docs/responseExamples/isStudySubjectExistingSubject.xml");
        IsStudySubjectResponseHandlerTests.subjectPresentResponse = messageFactory.createMessage(null,
                new FileInputStream(isStudySubjectExistingSubject.toFile()));

        Path isStudySubjectNonExistingSubject = Paths.get("docs/responseExamples/isStudySubjectNonExistingSubject.xml");
        IsStudySubjectResponseHandlerTests.subjectAbsentResponse = messageFactory.createMessage(null,
                new FileInputStream(isStudySubjectNonExistingSubject.toFile()));
    }
}
