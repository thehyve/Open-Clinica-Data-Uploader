package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class ItemGroupDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean repeating;

    private String oid;

    private String name;


    private boolean ungrouped = false;

    public boolean isUngrouped() {
        return ungrouped;
    }

    public void setUngrouped(boolean ungrouped) {
        this.ungrouped = ungrouped;
    }

    public List<String> getMandatoryItems() {
        return mandatoryItems;
    }

    public void setMandatoryItems(List<String> mandatoryItems) {
        this.mandatoryItems = mandatoryItems;
    }

    @Transient
    private List<String> mandatoryItems = new ArrayList<>();

    public boolean isMandatoryInCrf() {
        return true; // In OpenClinica interpretation of the ODM all ItemGroups are mandatory
    }


    @OneToMany(targetEntity = ItemDefinition.class, cascade = CascadeType.ALL)
    private List<ItemDefinition> items = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ItemDefinition> getItems() {
        return items;
    }

    public void setItems(List<ItemDefinition> items) {
        this.items = items;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
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

    public ItemGroupDefinition() {
    }

    public ItemGroupDefinition(ItemGroupDefinition prototype) {
        this.repeating = prototype.isRepeating();
        this.name = prototype.getName();
        this.oid = prototype.getOid();
        this.items = new ArrayList<>(prototype.getItems());
        this.mandatoryItems = prototype.getMandatoryItems();
        this.ungrouped = prototype.isUngrouped();
    }

    public void addItem(ItemDefinition item) {
        this.items.add(item);
    }
}
