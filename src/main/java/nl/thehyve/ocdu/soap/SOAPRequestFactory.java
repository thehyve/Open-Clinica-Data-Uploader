package nl.thehyve.ocdu.soap;


import nl.thehyve.ocdu.models.OCEntities.Study;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.soap.SOAPRequestDecorators.*;
import nl.thehyve.ocdu.soap.SOAPRequestFactories.CreateSubjectRequestFactory;
import org.openclinica.ws.beans.EventType;
import org.openclinica.ws.beans.ListStudySubjectsInStudyType;
import org.openclinica.ws.beans.StudyRefType;
import org.openclinica.ws.studysubject.v1.CreateRequest;
import org.openclinica.ws.studysubject.v1.ObjectFactory;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.dom.DOMResult;
import java.util.Collection;


/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class SOAPRequestFactory {

    private String apiVersion = "v1"; // OpenClinica uses v1 for all versions, no need to make it configurable

    private static final String STUDY_NAME_SPACE = "http://openclinica.org/ws/study/";

    private static final String STUDY_SUBJECT_NAME_SPACE = "http://openclinica.org/ws/studySubject/";

    private static final String IMPORT_DATA_NAME_SPACE = "http://openclinica.org/ws/data/";

    private static final String EVENT_NAME_SPACE = "http://openclinica.org/ws/event/";

    public SOAPMessage createListStudiesRequest(String username, String passwordHash) throws Exception {
        SOAPMessage soapMessage = getSoapMessage(username, passwordHash, STUDY_NAME_SPACE);
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        listAllStudiesRequestDecorator decorator = new listAllStudiesRequestDecorator();
        decorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
    }

    public SOAPMessage createGetStudyMetadataRequest(String username, String passwordHash, Study study) throws Exception {
        SOAPMessage soapMessage = getSoapMessage(username, passwordHash, STUDY_NAME_SPACE);
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        GetStudyMetadataRequestDecorator decorator = new GetStudyMetadataRequestDecorator();
        decorator.setStudy(study);
        decorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
    }


    public SOAPMessage createDataUploadRequest(String userName, String passwordHash, String odmString) throws Exception {
        SOAPMessage soapMessage = getSoapMessage(userName, passwordHash, IMPORT_DATA_NAME_SPACE);
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        ImportDataRequestDecorator importDataRequestDecorator =
                new ImportDataRequestDecorator(odmString);
        importDataRequestDecorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
    }

    public SOAPMessage createIsStudySubjectRequest(String userName, String passwordHash, String studyLabel, String subjectLabel) throws Exception {
        SOAPMessage soapMessage = getSoapMessage(userName, passwordHash, STUDY_SUBJECT_NAME_SPACE);
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        IsStudySubjectRequestDecorator isStudySubjectRequestDecorator =
                new IsStudySubjectRequestDecorator(subjectLabel, studyLabel);

        isStudySubjectRequestDecorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
    }


    public SOAPMessage createListAllByStudy(String userName, String passwordHash, Study study) throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();

        ListStudySubjectsInStudyType listStudySubjectsInStudyType = new ListStudySubjectsInStudyType();
        StudyRefType studyRefType = new StudyRefType();
        studyRefType.setIdentifier(study.getIdentifier());
        listStudySubjectsInStudyType.setStudyRef(studyRefType);

        JAXBElement<ListStudySubjectsInStudyType> body = objectFactory.createListAllByStudyRequest(listStudySubjectsInStudyType);

        SOAPMessage soapMessage = getSoapMessage(userName, passwordHash, STUDY_NAME_SPACE);

        Document doc = convertToDocument(body, ListStudySubjectsInStudyType.class);
        soapMessage.getSOAPBody().addDocument(doc);
        return soapMessage;
    }

    public SOAPMessage createCreateSubject(String userName, String passwordHash, Collection<Subject> subjects
            ) throws Exception {
        CreateSubjectRequestFactory factory = new CreateSubjectRequestFactory();
        Collection<JAXBElement<CreateRequest>> createRequests =  factory.getCreateRequests(subjects);
        SOAPMessage soapMessage = getSoapMessage(userName, passwordHash, STUDY_SUBJECT_NAME_SPACE);
        for(JAXBElement createRequest: createRequests) {
            Document xmlDoc = convertToDocument(createRequest, CreateRequest.class);
            soapMessage.getSOAPBody().addDocument(xmlDoc);
        }
        return soapMessage;
    }


    public SOAPMessage createScheduleEventRequest(String userName, String passwordHash, EventType eventType) throws Exception {

        SOAPMessage soapMessage = getSoapMessage(userName, passwordHash, EVENT_NAME_SPACE);
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        ScheduleEventRequestDecorator scheduleEventRequestDecorator = new ScheduleEventRequestDecorator(eventType);

        scheduleEventRequestDecorator.decorateBody(envelope);
        soapMessage.saveChanges();

        return soapMessage;
    }

    private Document convertToDocument(JAXBElement<?> jaxbElement, Class... expectedClasses) throws Exception {
        DOMResult res = new DOMResult();
        JAXBContext context = JAXBContext.newInstance(expectedClasses);
        context.createMarshaller().marshal(jaxbElement, res);
        return (Document) res.getNode();
    }

    //TODO: move decorateHeader to an abstract class implementing SoapDecorator and make other decorators extend it.
    private void decorateHeader(SOAPEnvelope envelope, String username, String passwordHash) throws Exception {
        SOAPHeader header = envelope.getHeader();
        SOAPHeaderElement security = header.addHeaderElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse"));
        security.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        security.setMustUnderstand(true);
        SOAPElement usrToken = security.addChildElement("UsernameToken", "wsse");
        usrToken.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), "UsernameToken-27777511");
        SOAPElement usr = usrToken.addChildElement("Username", "wsse");
        usr.setTextContent(username);
        SOAPElement password = usrToken.addChildElement("Password", "wsse");
        password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        password.setTextContent(passwordHash);
    }


    private void decorateEnvelope(SOAPEnvelope envelope, String nameSpace) throws Exception {
        envelope.addNamespaceDeclaration(apiVersion, nameSpace  + apiVersion);
        envelope.addNamespaceDeclaration("beans", "http://openclinica.org/ws/beans");
    }

    private SOAPMessage getSoapMessage(String username, String passwordHash, String nameSpace) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        decorateEnvelope(envelope, nameSpace);
        decorateHeader(envelope, username, passwordHash);

        return soapMessage;
    }


}
