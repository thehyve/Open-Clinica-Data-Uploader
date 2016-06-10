package nl.thehyve.ocdu.services;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import nl.thehyve.ocdu.models.OCEntities.ClinicalData;
import nl.thehyve.ocdu.models.OCEntities.DataNode;
import nl.thehyve.ocdu.models.OCEntities.Subject;
import nl.thehyve.ocdu.models.OcDefinitions.ItemGroupDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.models.OcDefinitions.ODMElement;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by jacob on 6/8/16.
 */
@Service
public class ODMService {

    private static final String ODM_DOCUMENT_NS = "http://www.cdisc.org/ns/odm/v1.3";

    private static final Logger log = LoggerFactory.getLogger(ODMService.class);

    public String generateODM(List<ClinicalData> clinicalDataList, MetaData metaData) throws Exception {
        StringBuffer odmDocument = buildDataTree(clinicalDataList);
        return odmDocument.toString();
    }


    private StringBuffer createODMDocument(String studyOID) throws Exception {
        StringBuffer ret = new StringBuffer();
        ret.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        ret.append("<ODM xmlns=\"http://www.cdisc.org/ns/odm/v1.3\" ");
        ret.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        ret.append("ODMVersion=\"1.3\" ");
        ret.append("FileOID=\"");
        ret.append(System.currentTimeMillis() + "");
        ret.append("\" ");
        ret.append("FileType=\"Snapshot\" ");
        ret.append("Description=\"Dataset ODM\" ");
        ret.append("CreationDateTime=\"");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        String dateTimeStamp = df.format(GregorianCalendar.getInstance().getTime());
        ret.append(dateTimeStamp);
        ret.append("\">");
        ret.append("<ClinicalData StudyOID=\"");
        ret.append(studyOID);
        ret.append("\" ");
        ret.append("MetaDataVersionOID=\"v1.0.0\">");
        return ret;
    }

    private StringBuffer buildDataTree(List<ClinicalData> clinicalDataList) throws Exception {
        long startTime = System.currentTimeMillis();
        if (clinicalDataList.size() == 0) {
            return new StringBuffer("");
        }

        DataNode root = new DataNode("root", "root");

        for (ClinicalData clinicalData : clinicalDataList) {
            DataNode studyNode = new DataNode("StudyData", clinicalData.getStudy());
            root.addChild(studyNode);
            studyNode = root.findChild(studyNode);

            DataNode subjectDataNode = new DataNode("SubjectData", clinicalData.getSsid());
            studyNode.addChild(subjectDataNode);
            subjectDataNode = studyNode.findChild(subjectDataNode);

            DataNode eventNode = new DataNode("StudyEventData", clinicalData.getEventName());
            subjectDataNode.addChild(eventNode);
            eventNode = subjectDataNode.findChild(eventNode);

            DataNode eventRepeatNode = new DataNode("StudyEventRepeatKey", clinicalData.getEventRepeat() + "");
            eventNode.addChild(eventRepeatNode);
            eventRepeatNode = eventNode.findChild(eventRepeatNode);


            DataNode crfNode = new DataNode("FormData", clinicalData.getCrfName());
            eventRepeatNode.addChild(crfNode);
            crfNode = eventRepeatNode.findChild(crfNode);


            // TODO: add the ItemGroup to the ClinicalData and this section of code!!!
            DataNode itemDataNode = new DataNode("ItemData", clinicalData.getItem());
            crfNode.addChild(itemDataNode);
            //itemDataNode = crfNode.findChild(itemDataNode);
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println(">>>>>" + duration);
        return new StringBuffer("");
    }

    private String toString(Document document) throws Exception {
        StringWriter stringOut = new StringWriter();
        OutputFormat format = new OutputFormat();
        format.setLineWidth(120);
        format.setIndenting(true);
        format.setIndent(4);
        format.setEncoding("UTF-8");
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        serial.serialize(document);
        return stringOut.toString();

    }


    public class DataTree {
        private String name;

        private String xmlElementName;
        //private MetaDataTree parent;
        private List<DataTree> children = new ArrayList<>();

        public DataTree(String name, String xmlElementName) {
            this.name = name;
            this.xmlElementName = xmlElementName;
        }

        public DataTree() {
        }

        public String getName() {
            return name;
        }



        public List<DataTree> getChildren() {
            return children;
        }

        public void setChildren(List<DataTree> children) {
            this.children = children;
        }

        public void addChild(DataTree node) {
            this.children.add(node);
        }
    }
}
