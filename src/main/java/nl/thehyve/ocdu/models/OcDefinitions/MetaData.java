package nl.thehyve.ocdu.models.OcDefinitions;

import nl.thehyve.ocdu.models.OCEntities.PersonIDUsage;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String studyOID;

    private String studyName;

    @OneToMany(targetEntity = EventDefinition.class, cascade = CascadeType.ALL)
    private List<EventDefinition> eventDefinitions;

    @OneToMany(targetEntity = ItemGroupDefinition.class, cascade = CascadeType.ALL)
    private List<ItemGroupDefinition> itemGroupDefinitions;

    @OneToMany(targetEntity = CodeListDefinition.class, cascade = CascadeType.ALL)
    private List<CodeListDefinition> codeListDefinitions;

    @OneToMany(targetEntity = SiteDefinition.class, cascade = CascadeType.ALL)
    private List<SiteDefinition> siteDefinitions = new ArrayList<>();
    private String status;
    private String protocolName;

    public void addEventDefinition(EventDefinition eventDef) {
        eventDefinitions.add(eventDef);
    }

    public void removeEventDefinition(EventDefinition eventDef) {
        eventDefinitions.remove(eventDef);
    }


    public void addItemGroupDefinition(ItemGroupDefinition itemGroupDef) {
        itemGroupDefinitions.add(itemGroupDef);
    }

    public void removeItemGroupDefinition(ItemGroupDefinition itemGroupDef) {
        itemGroupDefinitions.remove(itemGroupDef);
    }

    public void addCodeListDefinition(CodeListDefinition codeListDefinition) {
        codeListDefinitions.add(codeListDefinition);
    }

    public void removeCodeListDefinition(CodeListDefinition codeListDefinition) {
        codeListDefinitions.remove(codeListDefinition);
    }

    /**
     * Returns a clinical data form's OID based on the name and version.
     * @param crfName the form name
     * @param crfVersion the form version
     * @return an empty {@link String} if the crfName or crfVersion are empty or if
     * the form's OID can not be found. In other cases it returns the form's OID.
     */
    public String findFormOID(String crfName, String crfVersion) {
        if ((StringUtils.isEmpty(crfName)) ||
                (StringUtils.isEmpty(crfVersion))) {
            return "";
        }
        for (EventDefinition eventDefinition : eventDefinitions) {
            for (CRFDefinition crfDefinition : eventDefinition.getCrfDefinitions()) {
                if ((crfName.equals(crfDefinition.getName())) &&
                    (crfVersion.equals(crfDefinition.getVersion()))) {
                    return crfDefinition.getOid();
                }
            }
        }
        return "";
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudyOID() {
        return studyOID;
    }

    public void setStudyOID(String studyOID) {
        this.studyOID = studyOID;
    }

    public List<EventDefinition> getEventDefinitions() {
        return eventDefinitions;
    }

    public void setEventDefinitions(List<EventDefinition> eventDefinitions) {
        this.eventDefinitions = eventDefinitions;
    }

    public List<ItemGroupDefinition> getItemGroupDefinitions() {
        return itemGroupDefinitions;
    }

    public void setItemGroupDefinitions(List<ItemGroupDefinition> itemGroupDefinitions) {
        this.itemGroupDefinitions = itemGroupDefinitions;
    }

    public List<CodeListDefinition> getCodeListDefinitions() {
        return codeListDefinitions;
    }

    public void setCodeListDefinitions(List<CodeListDefinition> codeListDefinitions) {
        this.codeListDefinitions = codeListDefinitions;
    }

    public List<SiteDefinition> getSiteDefinitions() {
        return siteDefinitions;
    }

    private boolean genderRequired;

    private PersonIDUsage personIDUsage;

    private int birthdateRequired;

    public PersonIDUsage getPersonIDUsage() {
        return personIDUsage;
    }

    public void setPersonIDUsage(PersonIDUsage personIDUsage) {
        this.personIDUsage = personIDUsage;
    }

    public boolean isGenderRequired() {
        return genderRequired;
    }

    public void setGenderRequired(boolean genderRequired) {
        this.genderRequired = genderRequired;
    }

    public int getBirthdateRequired() {
        return birthdateRequired;
    }

    public void setBirthdateRequired(int birthdateRequired) {
        this.birthdateRequired = birthdateRequired;
    }

    public void setSiteDefinitions(List<SiteDefinition> siteDefinitions) {
        this.siteDefinitions = siteDefinitions;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public MetaData() {
        this.codeListDefinitions = new ArrayList<>();
        this.itemGroupDefinitions = new ArrayList<>();
        this.eventDefinitions = new ArrayList<>();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setLocationRequirementSetting(ProtocolFieldRequirementSetting protocolFieldRequirementSetting) {
        this.locationRequirementSetting = protocolFieldRequirementSetting;
    }

    public ProtocolFieldRequirementSetting getLocationRequirementSetting() {
        return locationRequirementSetting;
    }

    private ProtocolFieldRequirementSetting locationRequirementSetting = ProtocolFieldRequirementSetting.OPTIONAL;

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    public String getProtocolName() {
        return protocolName;
    }
}
