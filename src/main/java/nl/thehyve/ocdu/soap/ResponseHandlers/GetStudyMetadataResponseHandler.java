package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.OcDefinitions.*;
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
    public static final String itemGroupDefSelector = "//MetaDataVersion/ItemGroupDef";
    public static final String ITEM_DEFINITION_SELECTOR = "//MetaDataVersion/ItemDef";
    public static final String odmSelector = "//createResponse/odm";
    public static final String presentInEventSelector = ".//*[local-name()='PresentInEventDefinition']";
    public static final String presentInCrfsSelector = ".//*[local-name()='PresentInForm']";
    public static final String CRF_VERSION_SELECTOR = ".//*[local-name()='VersionDescription']/text()[1]";
    public static final String itemGroupRefSelector = ".//*[local-name()='ItemGroupRef']";
    public static final String itemRefSelector = ".//*[local-name()='ItemRef']";


    public static MetaData parseGetStudyMetadataResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document odm = getOdm(response);
        if (odm == null) {
            return null;
        }

        MetaData metaData = new MetaData();
        metaData.setStudyIdentifier("Study");//TODO: add retrieving study identifier from the metadata
        NodeList crfDefsNodes = (NodeList) xpath.evaluate(crfDefSelector, odm, XPathConstants.NODESET);
        NodeList eventDefsNodes = (NodeList) xpath.evaluate(eventDefSelector, odm, XPathConstants.NODESET);
        NodeList itemGroupDefNodes = (NodeList) xpath.evaluate(itemGroupDefSelector, odm, XPathConstants.NODESET);
        NodeList itemDefNodes = (NodeList) xpath.evaluate(ITEM_DEFINITION_SELECTOR, odm, XPathConstants.NODESET);

        Map eventMap = parseEvents(eventDefsNodes);
        List<CRFDefinition> crfDefs = parseCrfs(crfDefsNodes, eventMap);
        addToEvent(crfDefs, eventMap, eventDefsNodes); // Mandatory in event is defined in EventDef

        List<EventDefinition> events = new ArrayList<>();
        events.addAll(eventMap.values());

        List<ItemDefinition> items = parseItemDefinitions(itemDefNodes);
        List<ItemGroupDefinition> itemGroups = parseItemGroupDefinitions(itemGroupDefNodes, crfDefs, items);

        metaData.setEventDefinitions(events);
        metaData.setItemGroupDefinitions(itemGroups);
        return metaData;
    }

    private static void addToEvent(List<CRFDefinition> crfDefs, Map<String, EventDefinition> events, NodeList eventDefsNodes) throws XPathExpressionException {
        for (int i = 0; i < eventDefsNodes.getLength(); i++) {
            Node item = eventDefsNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            NodeList formRefs = (NodeList) xpath.evaluate("./FormRef", item, XPathConstants.NODESET);
            for (int j = 0; j < formRefs.getLength(); j++) {
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
        if (odmCDATANode == null) {
            return null;
        }
        String textContent = odmCDATANode.getTextContent(); //TODO: Add handling case when no ODM is served by PC
        Document odm = SoapUtils.unEscapeCDATAXML(textContent);
        return odm;
    }


    private static Map<String, EventDefinition> parseEvents(NodeList eventDefsNodes) {
        HashMap<String, EventDefinition> events = new HashMap<>();
        for (int i = 0; i < eventDefsNodes.getLength(); i++) {
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

    private static List<CRFDefinition> parseCrfs(NodeList crfDefsNodes, Map<String, EventDefinition> events) throws XPathExpressionException {
        List<CRFDefinition> crfs = new ArrayList<>();
        for (int i = 0; i < crfDefsNodes.getLength(); i++) {
            Node crfNode = crfDefsNodes.item(i);
            String oid = crfNode.getAttributes().getNamedItem("OID").getTextContent();
            String name = crfNode.getAttributes().getNamedItem("Name").getTextContent();
            String version = getCrfVersion(name);
            name = parseCrfName(name);
            String repeatingText = crfNode.getAttributes().getNamedItem("Repeating").getTextContent();
            boolean repeating = false;
            if (repeatingText.equals("Yes")) {
                repeating = true;
            }

            CRFDefinition newCrf = new CRFDefinition();
            newCrf.setName(name);
            newCrf.setOid(oid);
            newCrf.setRepeating(repeating);
            newCrf.setVersion(version);
            List<String> mandatoryItemGroups = getMandatory(crfNode, itemGroupRefSelector, "ItemGroupOID");
            newCrf.setMandatoryItemGroups(mandatoryItemGroups);
            crfs.addAll(getCrfsInEvent(crfNode, newCrf, events)); // CRF Entity exists per Event
        }
        return crfs;
    }

    private static String parseCrfName(String name) {
        int cutIndex = name.lastIndexOf(" - ");
        return name.substring(0, cutIndex);
    }

    private static List<String> getMandatory(Node node, String xpathSelector, String attributeName) throws XPathExpressionException {
        NodeList itemRefs = (NodeList) xpath.evaluate(xpathSelector, node, XPathConstants.NODESET);
        List<String> mandatoryGroups = new ArrayList<>();
        for (int i = 0; i < itemRefs.getLength(); i++) {
            Node ref = itemRefs.item(i);
            String itemOID = ref.getAttributes().getNamedItem(attributeName).getTextContent();
            String mandatoryText = ref.getAttributes().getNamedItem("Mandatory").getTextContent();
            if (mandatoryText.equals("Yes")) {
                mandatoryGroups.add(itemOID);
            }
        }
        return mandatoryGroups;
    }

    private static List<String> getItems(Node node, String xpathSelector, String attributeName) throws XPathExpressionException {
        NodeList itemRefs = (NodeList) xpath.evaluate(xpathSelector, node, XPathConstants.NODESET);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < itemRefs.getLength(); i++) {
            Node ref = itemRefs.item(i);
            String itemOID = ref.getAttributes().getNamedItem(attributeName).getTextContent();
            items.add(itemOID);
        }
        return items;
    }

    private static List<ItemDefinition> parseItemDefinitions(NodeList itemDefNodes) {
        List<ItemDefinition> items = new ArrayList<>();
        for (int i = 0; i < itemDefNodes.getLength(); i++) {
            Node item = itemDefNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            String name = item.getAttributes().getNamedItem("Name").getTextContent();
            String dataType = item.getAttributes().getNamedItem("DataType").getTextContent();
            Node length1 = item.getAttributes().getNamedItem("Length");
            String length = "0"; // Can be empty, zero means no restriction on length
            if (length1 != null) {
                length = length1.getTextContent();
            }
            ItemDefinition itemDef = new ItemDefinition();
            itemDef.setOid(oid);
            itemDef.setName(name);
            itemDef.setDataType(dataType);
            itemDef.setLength(Integer.parseInt(length));
            items.add(itemDef);
        }
        return items;
    }

    private static List<ItemGroupDefinition> parseItemGroupDefinitions(NodeList itemGroupDefNodes,
                                                                       List<CRFDefinition> crfs,
                                                                       List<ItemDefinition> items)
            throws XPathExpressionException {
        List<ItemGroupDefinition> itemGroupDefs = new ArrayList<>();
        for (int i = 0; i < itemGroupDefNodes.getLength(); i++) {
            Node itemGroupDefNode = itemGroupDefNodes.item(i);
            String oid = itemGroupDefNode.getAttributes().getNamedItem("OID").getTextContent();
            String name = itemGroupDefNode.getAttributes().getNamedItem("Name").getTextContent();
            String repeatingText = itemGroupDefNode.getAttributes().getNamedItem("Repeating").getTextContent();
            boolean repeating = false;
            if (repeatingText.equals("Yes")) {
                repeating = true;
            }

            ItemGroupDefinition groupDef = new ItemGroupDefinition();
            groupDef.setName(name);
            groupDef.setRepeating(repeating);
            groupDef.setOid(oid);
            List<String> mandatoryItems = getMandatory(itemGroupDefNode, itemRefSelector, "ItemOID");
            List<String> allItems = getItems(itemGroupDefNode, itemRefSelector, "ItemOID");
            addItems(groupDef, mandatoryItems, allItems, items);
            List<ItemGroupDefinition> itemGroupInCrf = getItemGroupInCrf(itemGroupDefNode, groupDef, crfs);
            itemGroupDefs.addAll(itemGroupInCrf);
        }
        return itemGroupDefs;
    }

    private static void addItems(ItemGroupDefinition groupDef,
                                 List<String> mandatoryItems,
                                 List<String> allItems,
                                 List<ItemDefinition> allDefinedItems) {
        allDefinedItems.stream().filter(itemDefinition -> allItems.contains(itemDefinition.getOid()))
                .forEach(itemDefinition -> {
                    ItemDefinition item = new ItemDefinition(itemDefinition);
                    if (mandatoryItems.contains(itemDefinition.getOid())) {
                        item.setMandatoryInGroup(true);
                    }
                    groupDef.addItem(item);
                });
    }

    private static List<ItemGroupDefinition> getItemGroupInCrf(Node itemGroupDefNode,
                                                               ItemGroupDefinition prototype,
                                                               List<CRFDefinition> crfs) throws XPathExpressionException {
        ArrayList<ItemGroupDefinition> itemGroupDefs = new ArrayList<>();
        NodeList itemGroupNodes = (NodeList) xpath.evaluate(presentInCrfsSelector,
                itemGroupDefNode, XPathConstants.NODESET);
        for (int i = 0; i < itemGroupNodes.getLength(); i++) {
            Node node = itemGroupNodes.item(i);
            String formOID = node.getAttributes().getNamedItem("FormOID").getTextContent();
            crfs.stream()
                    .filter(crfDefinition -> crfDefinition.getOid().equals(formOID))
                    .forEach(crfDefinition -> {
                        ItemGroupDefinition groupDef = new ItemGroupDefinition(prototype);
                        if (crfDefinition.getMandatoryItemGroups().contains(prototype.getOid())) {
                            groupDef.setMandatoryInCrf(true);
                        }
                        crfDefinition.addItemGroupDef(groupDef);
                        itemGroupDefs.add(groupDef);
                    });

        }
        return itemGroupDefs;
    }


    private static String getCrfVersion(String name) {
        String version = "";
        String[] split = name.split(" - "); // Open clinica encodes version inside name ...
        if (split.length > 1) {
            version = split[split.length - 1];
        }
        return version;
    }

    private static List<CRFDefinition> getCrfsInEvent(Node crfNode, CRFDefinition prototype, Map<String, EventDefinition> events) {
        ArrayList<CRFDefinition> crfs = new ArrayList<>();
        try {
            NodeList crfNodes = (NodeList) xpath.evaluate(presentInEventSelector,
                    crfNode, XPathConstants.NODESET);
            for (int i = 0; i < crfNodes.getLength(); i++) {
                Node node = crfNodes.item(i);
                CRFDefinition crf = new CRFDefinition(prototype);
                String studyEventOID = node.getAttributes().getNamedItem("StudyEventOID").getTextContent();
                crf.setEvent(events.get(studyEventOID));
                String hidden = node.getAttributes().getNamedItem("StudyEventOID").getTextContent();//TODO: replace with XPATH selector
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
