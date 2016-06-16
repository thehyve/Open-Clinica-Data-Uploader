package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class ItemDefinition implements ODMElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String oid;
    private String name;
    private String dataType;
    private int length;
    private boolean mandatoryInGroup = false;
    private boolean isMultiselect = false;
    private  String codeListRef;

    @OneToMany(targetEntity = RangeCheck.class)
    private List<DisplayRule> displayRules = new ArrayList<>();

    public List<DisplayRule> getDisplayRules() {
        return displayRules;
    }

    public void setDisplayRules(List<DisplayRule> displayRules) {
        this.displayRules = displayRules;
    }

    public String getCodeListRef() {
        return codeListRef;
    }

    public void setCodeListRef(String codeListRef) {
        this.codeListRef = codeListRef;
    }

    public boolean isMultiselect() {
        return isMultiselect;
    }

    public void setMultiselect(boolean multiselect) {
        isMultiselect = multiselect;
    }

    private int significantDigits = 0;

    public int getSignificantDigits() {
        return significantDigits;
    }

    public void setSignificantDigits(int significantDigits) {
        this.significantDigits = significantDigits;
    }

    @OneToMany(targetEntity = RangeCheck.class)
    private List<RangeCheck> rangeCheckList;

    public boolean isMandatoryInGroup() {
        return mandatoryInGroup;
    }

    public void setMandatoryInGroup(boolean mandatoryInGroup) {
        this.mandatoryInGroup = mandatoryInGroup;
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemDefinition() {
    }

    public ItemDefinition(ItemDefinition prototype) {
        this.name = prototype.getName();
        this.length = prototype.getLength();
        this.oid = prototype.getOid();
        this.mandatoryInGroup = prototype.isMandatoryInGroup();
        this.dataType = prototype.getDataType();
        this.rangeCheckList = prototype.getRangeCheckList();
        this.significantDigits = prototype.getSignificantDigits();
        this.isMultiselect = prototype.isMultiselect();
        this.codeListRef = prototype.getCodeListRef();
        this.displayRules = prototype.getDisplayRules();
    }

    public List<RangeCheck> getRangeCheckList() {
        return rangeCheckList;
    }

    public void setRangeCheckList(List<RangeCheck> rangeCheckList) {
        this.rangeCheckList = rangeCheckList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDefinition that = (ItemDefinition) o;

        return oid != null ? oid.equals(that.oid) : that.oid == null;

    }

    @Override
    public int hashCode() {
        return oid != null ? oid.hashCode() : 0;
    }
}
