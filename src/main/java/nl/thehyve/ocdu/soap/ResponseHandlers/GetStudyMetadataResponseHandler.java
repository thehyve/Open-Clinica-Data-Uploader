package nl.thehyve.ocdu.soap.ResponseHandlers;

import nl.thehyve.ocdu.models.OcDefinitions.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static nl.thehyve.ocdu.soap.ResponseHandlers.SoapUtils.toDocument;

/**
 * Created by piotrzakrzewski on 29/04/16.
 */
public class GetStudyMetadataResponseHandler extends OCResponseHandler {
    //TODO: break down this monolith ...
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
    public static final String rangeChecksSelector = ".//*[local-name()='RangeCheck']";
    public static final String CODELIST_DEFINITION_SELECTOR = "//MetaDataVersion/CodeList";
    public static final String MULTIPLE_SELECT_DEFINITION_SELECTOR = "//MetaDataVersion/*[local-name()='MultiSelectList']";
    public static final String STUDY_SELECTOR = "//Study[1]";
    public static final String SITES_SELECTOR = "//Study[position()>1]";
    public static final String ITEM_PRESENT_IN_FORM_SELECTOR = ".//*[local-name()='ItemPresentInForm']";
    public static final String STUDY_DESCRIPTION_SELECTOR = ".//*[local-name()='StudyDescriptionAndStatus']";
    public static final String STUDY_STATUS_SELECTOR = ".//*[local-name()='StudySytemStatus'][1]";


    public static MetaData parseGetStudyMetadataResponse(SOAPMessage response) throws Exception { //TODO: handle exception
        Document odm = getOdm(response);
        if (odm == null) {
            return null;
        }

        Node studyNode = (Node) xpath.evaluate(STUDY_SELECTOR, odm, XPathConstants.NODE);
        NodeList crfDefsNodes = (NodeList) xpath.evaluate(crfDefSelector, odm, XPathConstants.NODESET);
        NodeList eventDefsNodes = (NodeList) xpath.evaluate(eventDefSelector, odm, XPathConstants.NODESET);
        NodeList itemGroupDefNodes = (NodeList) xpath.evaluate(itemGroupDefSelector, odm, XPathConstants.NODESET);
        NodeList itemDefNodes = (NodeList) xpath.evaluate(ITEM_DEFINITION_SELECTOR, odm, XPathConstants.NODESET);
        Node studyDescNode = (Node) xpath.evaluate(STUDY_DESCRIPTION_SELECTOR, studyNode, XPathConstants.NODE);

        Map eventMap = parseEvents(eventDefsNodes);
        List<CRFDefinition> crfDefs = parseCrfs(crfDefsNodes, eventMap);
        addToEvent(crfDefs, eventMap, eventDefsNodes); // Mandatory in event is defined in EventDef

        assignUngroupedItems(itemDefNodes, crfDefs);
        List<EventDefinition> events = new ArrayList<>();
        events.addAll(eventMap.values());

        List<ItemDefinition> items = parseItemDefinitions(itemDefNodes);
        List<ItemGroupDefinition> itemGroups = parseItemGroupDefinitions(itemGroupDefNodes, crfDefs, items);

        String studyStatus = parseStudyStatus(studyDescNode);

        MetaData metaData = new MetaData();
        Optional<String> studyIdOpt = parseStudyOid(studyNode);
        if (studyIdOpt.isPresent()) {
            metaData.setStudyOID(studyIdOpt.get());
        }
        Optional<String> studyNameOpt = parseStudyName(studyNode);
        if (studyNameOpt.isPresent()) {
            metaData.setStudyName(studyNameOpt.get());
        }
        metaData.setEventDefinitions(events);
        metaData.setCodeListDefinitions(parseCodeListDefinitions(odm));
        metaData.setSiteDefinitions(parseSiteDefinitions(odm));
        metaData.setItemGroupDefinitions(itemGroups);
        String studyRequirementPath = STUDY_SELECTOR + "/MetaDataVersion";
        metaData.setGenderRequired(parseGenderRequired(odm, studyRequirementPath));
        metaData.setBirthdateRequired(parseDateOfBirthRequired(odm, studyRequirementPath));
        metaData.setStatus(studyStatus);
        Node studyRequirements = (Node) xpath.evaluate(studyRequirementPath , odm, XPathConstants.NODE);
        metaData.setLocationRequirementSetting(getLocationRequirements(studyRequirements));
        return metaData;
    }

    private static ProtocolFieldRequirementSetting getLocationRequirements(Node studyRequirements) throws XPathExpressionException {
        NodeList stdyParams = (NodeList) xpath.evaluate(".//*[local-name()='StudyParameterListRef']",
                studyRequirements, XPathConstants.NODESET);
            for (int j = 0; j < stdyParams.getLength(); j++) {
                Node config_child = stdyParams.item(j);
                NamedNodeMap attrs = config_child.getAttributes();
                if (attrs != null) {
                    Node listID_attr = attrs.getNamedItem("StudyParameterListID");
                    if (listID_attr != null && listID_attr.getNodeValue().equals("SPL_eventLocationRequired")) {
                        Node value_attr = attrs.getNamedItem("Value");
                        String isLocationRequired = value_attr.getNodeValue();
                        if (isLocationRequired.equals("not_used")) {
                            return ProtocolFieldRequirementSetting.BANNED;
                        } else if (isLocationRequired.equals("required")) {
                            return ProtocolFieldRequirementSetting.MANDATORY;
                        } else if (isLocationRequired.equals("optional")) {
                            return ProtocolFieldRequirementSetting.OPTIONAL;
                        }
                    }
                }
            }
        return ProtocolFieldRequirementSetting.OPTIONAL;
    }

    private static String parseStudyStatus(Node studyDescNode) throws XPathExpressionException {
        String unspecified = "UNSPECIFIED";
        if (studyDescNode == null) return unspecified;
        Node statusNode = (Node) xpath.evaluate(STUDY_STATUS_SELECTOR,
                studyDescNode, XPathConstants.NODE);
        if (statusNode == null) return unspecified;
        return statusNode.getTextContent();
    }

    private static List<SiteDefinition> parseSiteDefinitions(Document odm) throws XPathExpressionException {
        NodeList siteNodes = (NodeList) xpath.evaluate(SITES_SELECTOR, odm, XPathConstants.NODESET);

        int site_index = 2;

        ArrayList<SiteDefinition> siteDefinitions = new ArrayList<>();
        for (int i = 0; i < siteNodes.getLength(); i++) {
            Node siteNode = siteNodes.item(i);
            SiteDefinition siteDefinition = new SiteDefinition();
            Optional<String> siteOidOpt = parseStudyOid(siteNode);
            if (siteOidOpt.isPresent()) {
                siteDefinition.setSiteOID(siteOidOpt.get());
            }
            Optional<String> siteNameOpt = parseStudyName(siteNode);
            if (siteNameOpt.isPresent()) {
                siteDefinition.setName(siteNameOpt.get());
            }

            String site_path = "(//Study)[" + site_index + "]/MetaDataVersion";
            boolean genderRequired = parseGenderRequired(odm, site_path);
            int DOBRequired = parseDateOfBirthRequired(odm, site_path);
            siteDefinition.setGenderRequired(genderRequired);
            siteDefinition.setBirthdateRequired(DOBRequired);

            siteDefinitions.add(siteDefinition);

            site_index++;
        }
        return siteDefinitions;
    }

    private static Optional<String> parseStudyOid(Node studyNode) {
        NamedNodeMap siteNodeAttributes = studyNode.getAttributes();
        if (siteNodeAttributes != null) {
            Node oidAttr = siteNodeAttributes.getNamedItem("OID");
            if (oidAttr != null) {
                return Optional.of(oidAttr.getTextContent());
            }
        }
        return Optional.empty();
    }

    private static Optional<String> parseStudyName(Node studyNode) {
        Node globalVariablesNode = null;
        NodeList studyNodeChildNodes = studyNode.getChildNodes();
        for (int j = 0; j < studyNodeChildNodes.getLength(); j++) {
            Node childNode = studyNodeChildNodes.item(j);
            if (childNode.getNodeName().equals("GlobalVariables")) {
                globalVariablesNode = childNode;
                break;
            }
        }

        if (globalVariablesNode != null) {
            NodeList globalVariablesNodeChildNodes = globalVariablesNode.getChildNodes();
            for (int j = 0; j < globalVariablesNodeChildNodes.getLength(); j++) {
                Node childNode = globalVariablesNodeChildNodes.item(j);
                if (childNode.getNodeName().equals("StudyName")) {
                    return Optional.of(childNode.getTextContent());
                }
            }
        }
        return Optional.empty();
    }

    private static boolean parseGenderRequired(Document odm, String mypath) throws XPathExpressionException {
        boolean isGenderRequired = false;
        Node n = (Node) xpath.evaluate(mypath, odm, XPathConstants.NODE);
        Node study_details_node = null;
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String name = children.item(i).getNodeName();
            if (name.equals("OpenClinica:StudyDetails")) {
                study_details_node = children.item(i);
                break;
            }
        }

        Node param_config_node = null;
        if (study_details_node != null) {
            NodeList details_children = study_details_node.getChildNodes();
            for (int j = 0; j < details_children.getLength(); j++) {
                String name = details_children.item(j).getNodeName();
                if (name.equals("OpenClinica:StudyParameterConfiguration")) {
                    param_config_node = details_children.item(j);
                    break;
                }
            }
        }

        if (param_config_node != null) {
            NodeList config_children = param_config_node.getChildNodes();
            for (int j = 0; j < config_children.getLength(); j++) {
                Node config_child = config_children.item(j);
                NamedNodeMap attrs = config_child.getAttributes();
                if (attrs != null) {
                    Node listID_attr = attrs.getNamedItem("StudyParameterListID");
                    if (listID_attr != null && listID_attr.getNodeValue().equals("SPL_genderRequired")) {
                        Node value_attr = attrs.getNamedItem("Value");
                        String isGenderRequiredStr = value_attr.getNodeValue();
                        if (isGenderRequiredStr.equals("true")) {
                            isGenderRequired = true;
                            break;
                        }
                    }
                }//if
            }//for
        }//if

        return isGenderRequired;
    }

    private static int parseDateOfBirthRequired(Document odm, String mypath) throws XPathExpressionException {
        /*
         * 1. yes, required
         * 2. only year of birth
         * 3. not required
         */
        int isDOBRequired = 3;
        Node n = (Node) xpath.evaluate(mypath, odm, XPathConstants.NODE);
        Node study_details_node = null;
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            String name = children.item(i).getNodeName();
            if (name.equals("OpenClinica:StudyDetails")) {
                study_details_node = children.item(i);
                break;
            }
        }

        Node param_config_node = null;
        if (study_details_node != null) {
            NodeList details_children = study_details_node.getChildNodes();
            for (int j = 0; j < details_children.getLength(); j++) {
                String name = details_children.item(j).getNodeName();
                if (name.equals("OpenClinica:StudyParameterConfiguration")) {
                    param_config_node = details_children.item(j);
                    break;
                }
            }
        }

        if (param_config_node != null) {
            NodeList config_children = param_config_node.getChildNodes();
            for (int j = 0; j < config_children.getLength(); j++) {
                Node config_child = config_children.item(j);
                NamedNodeMap attrs = config_child.getAttributes();
                if (attrs != null) {
                    Node listID_attr = attrs.getNamedItem("StudyParameterListID");
                    if (listID_attr != null && listID_attr.getNodeValue().equals("SPL_collectDob")) {
                        Node value_attr = attrs.getNamedItem("Value");
                        String isDOBRequiredStr = value_attr.getNodeValue();
                        isDOBRequired = Integer.valueOf(isDOBRequiredStr);
                    }
                }//if
            }//for
        }//if

        return isDOBRequired;
    }

    private static List<CodeListDefinition> parseCodeListDefinitions(Document odm) throws XPathExpressionException {
        NodeList codeListNodes = (NodeList) xpath.evaluate(CODELIST_DEFINITION_SELECTOR, odm, XPathConstants.NODESET);
        NodeList multipleSelectNodes = (NodeList) xpath.evaluate(MULTIPLE_SELECT_DEFINITION_SELECTOR, odm,
                XPathConstants.NODESET);

        return parseCodeListDefinitions(codeListNodes, multipleSelectNodes);
    }

    private static List<CodeListDefinition> parseCodeListDefinitions(NodeList codeListNodes, NodeList multipleSelectNodes) throws XPathExpressionException {
        List<CodeListDefinition> codeListDefinitions = parseCodeListNodes(codeListNodes, ".//CodeListItem", "OID", "CodedValue");
        codeListDefinitions.addAll(parseCodeListNodes(multipleSelectNodes, ".//*[local-name()='MultiSelectListItem']", "ID", "CodedOptionValue"));
        return codeListDefinitions;
    }

    private static List<CodeListDefinition> parseCodeListNodes(NodeList codeListNodes, String xpathSelector, String idAttribute, String codeValueAttribute) throws XPathExpressionException {
        List<CodeListDefinition> codeLists = new ArrayList<>();
        for (int i = 0; i < codeListNodes.getLength(); i++) {
            Node codeListDefNode = codeListNodes.item(i);
            NodeList codes = (NodeList) xpath.evaluate(xpathSelector, codeListDefNode, XPathConstants.NODESET);
            CodeListDefinition codeList = new CodeListDefinition();
            codeList.setOcid(codeListDefNode.getAttributes().getNamedItem(idAttribute).getTextContent());
            List<CodeListItemDefinition> codeDefList = new ArrayList<>();
            for (int codeIndex = 0; codeIndex < codes.getLength(); codeIndex++) {
                Node codeNode = codes.item(codeIndex);
                String codedValue = codeNode.getAttributes().getNamedItem(codeValueAttribute).getTextContent();
                CodeListItemDefinition codeListItemDefinition = new CodeListItemDefinition();
                codeListItemDefinition.setContent(codedValue);
                codeDefList.add(codeListItemDefinition);
            }
            codeList.setItems(codeDefList);
            codeLists.add(codeList);
        }
        return codeLists;
    }


    private static void assignUngroupedItems(NodeList itemDefNodes, List<CRFDefinition> crfs) throws XPathExpressionException {
        HashMap<String, CRFDefinition> crfMap = new HashMap<>();
        crfs.stream().forEach(crfDefinition -> crfMap.put(crfDefinition.getOid(), crfDefinition));
        for (int i = 0; i < itemDefNodes.getLength(); i++) {
            Node item = itemDefNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            String formOIDs = item.getAttributes().getNamedItem("OpenClinica:FormOIDs").getTextContent();
            for (String crfOID : parseFromOIDs(formOIDs)) {
                CRFDefinition crfDefinition = crfMap.get(crfOID);
                List<String> mandatoryUngroupedItems = crfDefinition.getMandatoryUngroupedItems();
                ItemDefinition ungroupedItem = getItem(item);
                if (mandatoryUngroupedItems.contains(oid)) {
                    ungroupedItem.setMandatoryInGroup(true);
                } else ungroupedItem.setMandatoryInGroup(false);
                crfDefinition.addUngroupedItem(ungroupedItem);
            }
        }
    }

    private static Collection<String> parseFromOIDs(String formOIDs) {
        String[] split = formOIDs.split(",");
        List<String> crfsOids = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            crfsOids.add(split[i]);
        }
        return crfsOids;
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
        String result = isAuthFailure(document);
        if (!StringUtils.isEmpty(result)) {
            throw new AuthenticationCredentialsNotFoundException("Problem calling OpenClinica web-services: " + result);
        }
        Node odmCDATANode = (Node) xpath.evaluate(odmSelector, document, XPathConstants.NODE);
        if (odmCDATANode == null) {
            return null;
        }
        String textContent = odmCDATANode.getTextContent(); //TODO: Add handling case when no ODM is served by OC
        Document odm = SoapUtils.unEscapeCDATAXML(textContent);
        return odm;
    }


    private static Map<String, EventDefinition> parseEvents(NodeList eventDefsNodes) {
        HashMap<String, EventDefinition> events = new HashMap<>();
        for (int i = 0; i < eventDefsNodes.getLength(); i++) {
            Node item = eventDefsNodes.item(i);
            String oid = item.getAttributes().getNamedItem("OID").getTextContent();
            String name = item.getAttributes().getNamedItem("Name").getTextContent();
            String repeatingText = item.getAttributes().getNamedItem("Repeating").getTextContent();
            String type = item.getAttributes().getNamedItem("Type").getTextContent();
            boolean isRepeating = false;
            if (repeatingText.equals("Yes")) {
                isRepeating = true;
            }
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

    private static List<ItemDefinition> parseItemDefinitions(NodeList itemDefNodes) throws XPathExpressionException {
        List<ItemDefinition> items = new ArrayList<>();
        for (int i = 0; i < itemDefNodes.getLength(); i++) {
            Node item = itemDefNodes.item(i);
            ItemDefinition itemDef = getItem(item);
            items.add(itemDef);
        }
        return items;
    }

    private static List<DisplayRule> getDisplayRules(Node itemDefNode) throws XPathExpressionException {
        List<DisplayRule> displayRules = new ArrayList<>();
        NodeList itemPresentInFormNode = (NodeList) xpath.evaluate(ITEM_PRESENT_IN_FORM_SELECTOR, itemDefNode, XPathConstants.NODESET);
        for (int i = 0; i < itemPresentInFormNode.getLength(); i++) {
            Node item = itemPresentInFormNode.item(i);
            DisplayRule displayRule = getDisplayRule(item);
            displayRules.add(displayRule);
        }
        return displayRules;
    }

    private static DisplayRule getDisplayRule(Node itemPresentInFormNode) {
        Node formOIDNode = itemPresentInFormNode.getAttributes().getNamedItem("FormOID");
        Node showItemNode = itemPresentInFormNode.getAttributes().getNamedItem("ShowItem");
        NodeList childNodes = itemPresentInFormNode.getChildNodes();
        assert formOIDNode != null;
        assert showItemNode != null;

        DisplayRule rule = new DisplayRule();
        String crfOID = formOIDNode.getTextContent();
        boolean show = true;
        if (showItemNode.getTextContent().equals("No")) {
            show = false;
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeName().equals("ControlItemName")) {
                String item = child.getTextContent();
                rule.setControlItemName(item);
            } else if (child.getNodeName().equals("OptionValue")) {
                String item = child.getTextContent();
                rule.setOptionValue(item);
            }
        }
        rule.setAppliesInCrf(crfOID);
        rule.setShow(show);
        return rule;
    }

    private static ItemDefinition getItem(Node item) throws XPathExpressionException {
        String oid = item.getAttributes().getNamedItem("OID").getTextContent();
        String name = item.getAttributes().getNamedItem("Name").getTextContent();
        String dataType = item.getAttributes().getNamedItem("DataType").getTextContent();
        Node length1 = item.getAttributes().getNamedItem("Length");
        Node significantDigits = item.getAttributes().getNamedItem("SignificantDigits");
        String length = "0"; // Can be empty, zero means no restriction on length
        String significantDigitsText = "0";
        if (length1 != null) {
            length = length1.getTextContent();
        }
        if (significantDigits != null) {
            significantDigitsText = significantDigits.getTextContent();
        }
        List<RangeCheck> rangeChecks = parseRangeChecks(item);
        boolean isMultiSelect = isMultiSelect(item);
        String codeListRef = determineCodeListRef(item);
        ItemDefinition itemDef = new ItemDefinition();
        itemDef.setOid(oid);
        itemDef.setName(name);
        itemDef.setDataType(dataType);
        itemDef.setLength(Integer.parseInt(length));
        itemDef.setRangeCheckList(rangeChecks);
        itemDef.setSignificantDigits(Integer.parseInt(significantDigitsText));
        itemDef.setMultiselect(isMultiSelect);
        itemDef.setCodeListRef(codeListRef);
        return itemDef;
    }

    private static String determineCodeListRef(Node item) throws XPathExpressionException {
        Node refCodeList = (Node) xpath.evaluate(".//CodeListRef", item, XPathConstants.NODE);
        Node refMultiSelect = (Node) xpath.evaluate(".//*[local-name()='MultiSelectListRef']", item, XPathConstants.NODE);
        if (refCodeList != null) {
            return refCodeList.getAttributes().getNamedItem("CodeListOID").getTextContent();
        }
        if (refMultiSelect != null) {
            return refMultiSelect.getAttributes().getNamedItem("MultiSelectListID").getTextContent();
        }
        return null;
    }

    private static boolean isMultiSelect(Node item) throws XPathExpressionException {
        NodeList codeListRefs = (NodeList) xpath.evaluate(".//*[local-name()='MultiSelectListRef']", item, XPathConstants.NODESET);//TODO: make it into a constant at class level
        if (codeListRefs.getLength() > 0) {
            return true;
        } else
            return false;
    }

    private static List<RangeCheck> parseRangeChecks(Node item) throws XPathExpressionException {
        NodeList rangeChekNodes = (NodeList) xpath.evaluate(rangeChecksSelector, item, XPathConstants.NODESET);
        List<RangeCheck> rangeChecks = new ArrayList<>();
        for (int i = 0; i < rangeChekNodes.getLength(); i++) {
            Node rangeChecKnode = rangeChekNodes.item(i);
            String comparator = rangeChecKnode.getAttributes().getNamedItem("Comparator").getTextContent();
            Node valueNode = (Node) xpath.evaluate(".//CheckValue", rangeChecKnode, XPathConstants.NODE);
            String value = valueNode.getTextContent();
            RangeCheck rangeCheck = new RangeCheck();
            RangeCheck.COMPARATOR comparatorEnum = RangeCheck.COMPARATOR.valueOf(comparator);
            BigDecimal valueBigDec = BigDecimal.valueOf(Double.parseDouble(value));
            rangeCheck.setComparator(comparatorEnum);
            rangeCheck.setValue(valueBigDec);
            rangeChecks.add(rangeCheck);
        }
        return rangeChecks;
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
            if (oid.endsWith("UNGROUPED")) {
                groupDef.setUngrouped(true);
            }
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
                    item.setGroup(groupDef);
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
                        if (!groupDef.isUngrouped()) {
                            crfDefinition.addItemGroupDef(groupDef);
                        } else crfDefinition.addAllUngroupedItems(getItemNames(groupDef));
                        itemGroupDefs.add(groupDef);
                    });
        }
        return itemGroupDefs;
    }

    private static Set<String> getItemNames(ItemGroupDefinition groupDef) {
        return groupDef.getItems().stream().map(ItemDefinition::getOid).collect(Collectors.toSet());
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
                String hiddenText = node.getAttributes().getNamedItem("HideCRF").getTextContent();//TODO: replace with XPATH selector
                boolean hidden = false;
                if (hiddenText.equals("Yes")) {
                    hidden = true;
                }
                crf.setHidden(hidden);
                crfs.add(crf);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return crfs;
    }

    private static XPath xpath = XPathFactory.newInstance().newXPath();


}
