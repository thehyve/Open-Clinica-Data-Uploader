import javax.xml.namespace.QName;
import javax.xml.soap.*;

public class SOAPClientSAAJ {

    public static void main(String args[]) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        String url = "http://ocdu-openclinica-dev.thehyve.net/OpenClinica-ws/ws/study/v1";
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
        System.out.print("Response SOAP Message:");
        soapResponse.writeTo(System.out);

        soapConnection.close();
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("v1", "http://openclinica.org/ws/study/v1");

        decorateHeader(envelope);
        decorateBody(envelope);

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    static void decorateBody(SOAPEnvelope envelope) throws Exception {
        SOAPBody soapBody = envelope.getBody();
        soapBody.addChildElement("listAllRequest","v1");
    }

    static void decorateHeader(SOAPEnvelope envelope) throws Exception{
        SOAPHeader header = envelope.getHeader();
        SOAPHeaderElement security =  header.addHeaderElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security","wsse"));
        security.addNamespaceDeclaration("wsse","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        security.setMustUnderstand(true);
        SOAPElement usrToken = security.addChildElement("UsernameToken","wsse");
        usrToken.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd","Id","wsu"),"UsernameToken-27777511");
        SOAPElement usr = usrToken.addChildElement("Username","wsse");
        usr.setTextContent("piotr");
        SOAPElement password = usrToken.addChildElement("Password","wsse");
        password.setAttribute("Type","http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
        password.setTextContent("00db94dcb2c0f493a5447a761034312f29835561");
    }

}
