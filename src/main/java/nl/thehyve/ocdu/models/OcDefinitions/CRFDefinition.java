package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class CRFDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private EventDefinition event; // CRFDefinition Entity is per EventDefinition, because they can have different mandatory status per Event

    private boolean mandatoryInEvent;
    private boolean hidden;
    private String oid;
    private String name;
    private boolean repeating;
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventDefinition getEvent() {
        return event;
    }

    public void setEvent(EventDefinition event) {
        this.event = event;
    }

    public boolean isMandatoryInEvent() {
        return mandatoryInEvent;
    }

    public void setMandatoryInEvent(boolean mandatoryInEvent) {
        this.mandatoryInEvent = mandatoryInEvent;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public CRFDefinition(CRFDefinition prototype) {
        this.name = prototype.getName();
        this.oid = prototype.getOid();
        this.repeating = prototype.isRepeating();
        this.version = prototype.getVersion();
    }

    public CRFDefinition() {
    }
}
