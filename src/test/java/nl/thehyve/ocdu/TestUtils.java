package nl.thehyve.ocdu;

import nl.thehyve.ocdu.soap.ResponseHandlers.ListAllByStudyResponseHandler;
import org.openclinica.ws.beans.StudySubjectWithEventsType;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Utility class for unit-testing
 * Created by Jacob Rousseau on 20-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class TestUtils {

    public static List<StudySubjectWithEventsType> createStudySubjectWithEventList() throws Exception {
        File mockResponseListAllByStudyFile = new File("docs/responseExamples/listAllByStudyResponse.xml");
        InputStream mockResponseListAllByStudyFileInputStream = new FileInputStream(mockResponseListAllByStudyFile);

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), mockResponseListAllByStudyFileInputStream);
        return ListAllByStudyResponseHandler.retrieveStudySubjectsType(soapMessage);
    }
}
