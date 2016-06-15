package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transient
    private List<String> mandatoryItemGroups = new ArrayList<>();
    @Transient
    private List<String> mandatoryUngroupedItems = new ArrayList<>();

    @OneToMany(targetEntity = ItemGroupDefinition.class)
    private List<ItemGroupDefinition> itemGroups = new ArrayList<>();

    @OneToMany(targetEntity = ItemDefinition.class)
    private List<ItemDefinition> ungroupedItems = new ArrayList<>();

    public Set<ItemDefinition> allItems() {
        Set<ItemDefinition> all = new HashSet<>();
        itemGroups.stream().forEach(itemGroupDefinition -> {
            all.addAll(itemGroupDefinition.getItems());
        });
        all.addAll(ungroupedItems);
        return all;
    }


    public List<ItemDefinition> getUngroupedItems() {
        return ungroupedItems;
    }

    public void setUngroupedItems(List<ItemDefinition> ungroupedItems) {
        this.ungroupedItems = ungroupedItems;
    }

    public List<ItemGroupDefinition> getItemGroups() {
        return itemGroups;
    }

    public void setItemGroups(List<ItemGroupDefinition> itemGroups) {
        this.itemGroups = itemGroups;
    }

    public List<String> getMandatoryItemGroups() {
        return itemGroups.stream().map(ItemGroupDefinition::getName).collect(Collectors.toList());
    } // We explicitly ignore mandatory status of groups - all groups defined in CRF are mandatory

    public void setMandatoryItemGroups(List<String> mandatoryItemGroups) {
        this.mandatoryItemGroups = mandatoryItemGroups;
    }

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
        this.mandatoryItemGroups = prototype.getMandatoryItemGroups();
        this.itemGroups = prototype.getItemGroups();
        this.mandatoryUngroupedItems = prototype.getMandatoryUngroupedItems();
        this.ungroupedItems = prototype.getUngroupedItems();
        this.hidden = prototype.isHidden();
    }

    public CRFDefinition() {
    }

    public List<String> getMandatoryUngroupedItems() {
        return mandatoryUngroupedItems;
    }

    public void setMandatoryUngroupedItems(List<String> mandatoryUngroupedItems) {
        this.mandatoryUngroupedItems = mandatoryUngroupedItems;
    }

    public void addItemGroupDef(ItemGroupDefinition groupDef) {
        this.itemGroups.add(groupDef);
    }

    public void addAllUngroupedItems(Collection<String> itemNames) {
        mandatoryUngroupedItems.addAll(itemNames);
    }

    public void addUngroupedItem(ItemDefinition ungroupedItem) {
        ungroupedItems.add(ungroupedItem);
    }

    public Set<String> getMandatoryItemNames() {
        Set<String> allMandatoryInCRF = new HashSet<>();
        itemGroups
                .stream()
                .filter(itemGroupDefinition -> itemGroupDefinition.isMandatoryInCrf())
                .forEach(mandatoryGroup -> {
                    List<ItemDefinition> items = mandatoryGroup.getItems();
                    items.stream()
                            .filter(itemDefinition -> itemDefinition.isMandatoryInGroup())
                            .map(ItemDefinition::getName).forEach(itemName -> {
                                allMandatoryInCRF.add(itemName);
                            }
                    );
                });
        Set<String> ungrouped = ungroupedItems.stream().map(ItemDefinition::getName).collect(Collectors.toSet());
        allMandatoryInCRF.addAll(ungrouped);
        return ungrouped;
    }
}
