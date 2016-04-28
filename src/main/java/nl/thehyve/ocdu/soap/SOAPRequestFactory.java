package nl.thehyve.ocdu.soap;

import nl.thehyve.ocdu.soap.SOAPRequestDecorators.GetStudyMetadataRequestDecorator;
import nl.thehyve.ocdu.soap.SOAPRequestDecorators.listAllStudiesRequestDecorator;

import javax.xml.namespace.QName;
import javax.xml.soap.*;


/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class SOAPRequestFactory {

    private String apiVersion = "v1"; // OpenClinica uses v1 for all versions, no need to make it configurable

    public SOAPMessage createListStudiesRequest(String username, String passwordHash) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(apiVersion, "http://openclinica.org/ws/study/v1");

        decorateHeader(envelope, username, passwordHash);
        listAllStudiesRequestDecorator decorator = new listAllStudiesRequestDecorator();
        decorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
    }

    public SOAPMessage createGetStudyMetadataRequest(String username, String passwordHash) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(apiVersion, "http://openclinica.org/ws/study/v1");

        decorateHeader(envelope, username, passwordHash);
        GetStudyMetadataRequestDecorator decorator = new GetStudyMetadataRequestDecorator();
        decorator.decorateBody(envelope);

        soapMessage.saveChanges();

        return soapMessage;
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

}
