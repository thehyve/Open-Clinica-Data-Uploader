package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.CRFDefinition;
import nl.thehyve.ocdu.models.EventDefinition;
import nl.thehyve.ocdu.models.MetaData;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by piotrzakrzewski on 29/04/16.
 */
public class GetStudyMetadataResponseHandler extends OCResponseHandler {

    public static final String crfDefSelector = "//MetaDataVersion/FormDef";
    public static final String eventDefSelector = "//MetaDataVersion/StudyEventDef";
    public static final String odmSelector = "//createResponse/odm";
    public static final String presentInEventSelector = ".//*[local-name()='PresentInEventDefinition']";

    public static MetaData parseGetStudyMetadataResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document odm = getOdm(response);

        MetaData metaData = new MetaData();
        NodeList crfDefsNodes = (NodeList) xpath.evaluate(crfDefSelector, odm, XPathConstants.NODESET);
        NodeList eventDefsNodes = (NodeList) xpath.evaluate(eventDefSelector, odm, XPathConstants.NODESET);
        Map eventMap = parseEvents(eventDefsNodes);
        List<CRFDefinition> crfDefs = parseCrfs(crfDefsNodes, eventMap);
        addToEvent(crfDefs, eventMap ,eventDefsNodes); // Mandatory in event is defined in EventDef

        List<EventDefinition> events = new ArrayList<>();
        events.addAll(eventMap.values());

        metaData.setEventDefinitions( events);
        return metaData;
    }

    private static void addToEvent(List<CRFDefinition> crfDefs,Map<String,EventDefinition> events ,NodeList eventDefsNodes) throws XPathExpressionException {
        for(int i = 0; i < eventDefsNodes.getLength(); i++) {
            Node item = eventDefsNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            NodeList formRefs = (NodeList) xpath.evaluate("./FormRef", item, XPathConstants.NODESET);
            for (int j = 0; j < formRefs.getLength() ; j++) {
                Node formRef = formRefs.item(j);
                String formOID = formRef.getAttributes().getNamedItem("FormOID").getTextContent();
                String mandatory = formRef.getAttributes().getNamedItem("Mandatory").getTextContent();
                boolean isMandatory = Boolean.parseBoolean(mandatory);
                List<CRFDefinition> matchingCRFs = crfDefs.stream().
                        filter(crf -> crf.getEvent().getStudyEventOID().equals(oid) && crf.getOid().equals(formOID))
                        .collect(Collectors.toList());
                if (matchingCRFs.size() == 1) {
                    CRFDefinition crfDefinition = matchingCRFs.get(0);
                    crfDefinition.setMandatoryInEvent(isMandatory);
                    EventDefinition eventDefinition = events.get(oid);
                    eventDefinition.addCrfDef(crfDefinition);
                } else {
                    //TODO: handle missing ref or more than 1 refs
                }
            }
        }
    }

    public static Document getOdm(SOAPMessage response) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, SOAPException, TransformerException {
        Document document = toDocument(response);
        if (isAuthFailure(document)) {
            throw new AuthenticationCredentialsNotFoundException("Authentication against OpenClinica unsuccessfull");
        }
        Node odmCDATANode = (Node) xpath.evaluate(odmSelector, document, XPathConstants.NODE);
        String textContent = odmCDATANode.getTextContent();
        Document odm = SoapUtils.unEscapeCDATAXML(textContent);
        return odm;
    }


    private static Map<String, EventDefinition> parseEvents(NodeList eventDefsNodes) {
        HashMap<String, EventDefinition> events = new HashMap<>();
        for(int i = 0; i < eventDefsNodes.getLength(); i++) {
            Node item = eventDefsNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            String name = item.getAttributes().getNamedItem("Name").getTextContent();
            String repeating = item.getAttributes().getNamedItem("Repeating").getTextContent();
            String type = item.getAttributes().getNamedItem("Type").getTextContent();
            boolean isRepeating = Boolean.parseBoolean(repeating);
            EventDefinition event = new EventDefinition();
            event.setName(name);
            event.setStudyEventOID(oid);
            event.setRepeating(isRepeating);
            event.setType(type);
            events.put(oid, event);
        }
        return events;
    }

    private static List<CRFDefinition> parseCrfs(NodeList crfDefsNodes, Map<String, EventDefinition> events) {
        List<CRFDefinition> crfs = new ArrayList<>();
        for (int i = 0; i < crfDefsNodes.getLength(); i++) {
            Node crfNode = crfDefsNodes.item(i);
            String oid = crfNode.getAttributes().getNamedItem("OID").getTextContent();
            String name = crfNode.getAttributes().getNamedItem("Name").getTextContent();
            String repeatingText = crfNode.getAttributes().getNamedItem("Repeating").getTextContent();
            boolean repeating = false;
            if (repeatingText.equals("Yes")) {
                repeating = true;
            }
            CRFDefinition newCrf = new CRFDefinition();
            newCrf.setName(name);
            newCrf.setOid(oid);
            newCrf.setRepeating(repeating);
            crfs.addAll(getCrfsInEvent(crfNode, newCrf, events)); // CRF Entity exists per Event
        }
        return crfs;
    }

    private static List<CRFDefinition> getCrfsInEvent(Node crfNode, CRFDefinition prototype, Map<String, EventDefinition> events) {
        ArrayList<CRFDefinition> crfs = new ArrayList<>();
        try {
            NodeList crfNodes = (NodeList) xpath.evaluate(presentInEventSelector,
                    crfNode, XPathConstants.NODESET);
            for (int i = 0; i < crfNodes.getLength(); i ++) {
                Node node = crfNodes.item(i);
                CRFDefinition crf = new CRFDefinition(prototype);
                String studyEventOID = node.getAttributes().getNamedItem("StudyEventOID").getTextContent();
                crf.setEvent(events.get(studyEventOID));
                String hidden = node.getAttributes().getNamedItem("StudyEventOID").getTextContent();
                crf.setHidden(Boolean.parseBoolean(hidden));
                crfs.add(crf);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return crfs;
    }

    private static XPath xpath = XPathFactory.newInstance().newXPath();


}
