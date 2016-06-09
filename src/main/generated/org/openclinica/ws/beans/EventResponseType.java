
package org.openclinica.ws.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eventResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="studySubjectRef" type="{http://openclinica.org/ws/beans}studySubjectRefType"/>
 *         &lt;element name="studyRef" type="{http://openclinica.org/ws/beans}studyRefType"/>
 *         &lt;element name="eventDefinitionOID" type="{http://openclinica.org/ws/beans}customStringType"/>
 *         &lt;element name="occurrence" type="{http://openclinica.org/ws/beans}customStringType" minOccurs="0"/>
 *         &lt;element name="status" type="{http://openclinica.org/ws/beans}customStringType" minOccurs="0"/>
 *         &lt;element name="subjectEventStatus" type="{http://openclinica.org/ws/beans}customStringType" minOccurs="0"/>
 *         &lt;element name="location" type="{http://openclinica.org/ws/beans}customStringType"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="endTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *         &lt;element name="eventCrfInformation" type="{http://openclinica.org/ws/beans}eventCrfInformationList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventResponseType", propOrder = {
    "studySubjectRef",
    "studyRef",
    "eventDefinitionOID",
    "occurrence",
    "status",
    "subjectEventStatus",
    "location",
    "startDate",
    "startTime",
    "endDate",
    "endTime",
    "eventCrfInformation"
})
public class EventResponseType {

    @XmlElement(required = true)
    protected StudySubjectRefType studySubjectRef;
    @XmlElement(required = true)
    protected StudyRefType studyRef;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String eventDefinitionOID;
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String occurrence;
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String status;
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String subjectEventStatus;
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String location;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar startDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar startTime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar endTime;
    protected List<EventCrfInformationList> eventCrfInformation;

    /**
     * Gets the value of the studySubjectRef property.
     * 
     * @return
     *     possible object is
     *     {@link StudySubjectRefType }
     *     
     */
    public StudySubjectRefType getStudySubjectRef() {
        return studySubjectRef;
    }

    /**
     * Sets the value of the studySubjectRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudySubjectRefType }
     *     
     */
    public void setStudySubjectRef(StudySubjectRefType value) {
        this.studySubjectRef = value;
    }

    /**
     * Gets the value of the studyRef property.
     * 
     * @return
     *     possible object is
     *     {@link StudyRefType }
     *     
     */
    public StudyRefType getStudyRef() {
        return studyRef;
    }

    /**
     * Sets the value of the studyRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyRefType }
     *     
     */
    public void setStudyRef(StudyRefType value) {
        this.studyRef = value;
    }

    /**
     * Gets the value of the eventDefinitionOID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventDefinitionOID() {
        return eventDefinitionOID;
    }

    /**
     * Sets the value of the eventDefinitionOID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDefinitionOID(String value) {
        this.eventDefinitionOID = value;
    }

    /**
     * Gets the value of the occurrence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOccurrence() {
        return occurrence;
    }

    /**
     * Sets the value of the occurrence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOccurrence(String value) {
        this.occurrence = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the subjectEventStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectEventStatus() {
        return subjectEventStatus;
    }

    /**
     * Sets the value of the subjectEventStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectEventStatus(String value) {
        this.subjectEventStatus = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartTime(XMLGregorianCalendar value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndTime(XMLGregorianCalendar value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the eventCrfInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventCrfInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventCrfInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventCrfInformationList }
     * 
     * 
     */
    public List<EventCrfInformationList> getEventCrfInformation() {
        if (eventCrfInformation == null) {
            eventCrfInformation = new ArrayList<EventCrfInformationList>();
        }
        return this.eventCrfInformation;
    }

}
