package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class EventDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(targetEntity = CRFDefinition.class)
    private List crfDefinitions;

    private String studyEventOID;
    private String name;
    private int orderNumber;
    private boolean mandatory;
    private boolean repeating;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudyEventOID() {
        return studyEventOID;
    }

    public void setStudyEventOID(String studyEventOID) {
        this.studyEventOID = studyEventOID;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<CRFDefinition> getCrfDefinitions() {
        return crfDefinitions;
    }

    public void setCrfDefinitions(List crfDefinitions) {
        this.crfDefinitions = crfDefinitions;
    }

    public void addCrfDef(CRFDefinition crfDefinition) {
        this.crfDefinitions.add(crfDefinition);
    }

    public void removeCrfDef(CRFDefinition crfDefinition) {
        this.crfDefinitions.remove(crfDefinition);
    }

    public EventDefinition() {
        this.crfDefinitions = new ArrayList<>();
    }
}
