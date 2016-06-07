package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.List;

/**
 * Created by piotrzakrzewski on 07/06/16.
 */
@Entity
public class DisplayRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String controlItemName; // name of the item which value has to be checked against optionValue
    private String appliesInCrf;  // OIDs of CRF to which the DisplayRule apply
    private String optionValue;  // if controlItemName's value equals optionValue, then the item is shown
    private boolean show;

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getControlItemName() {
        return controlItemName;
    }

    public void setControlItemName(String controlItemName) {
        this.controlItemName = controlItemName;
    }

    public String getAppliesInCrf() {
        return appliesInCrf;
    }

    public void setAppliesInCrf(String appliesInCrf) {
        this.appliesInCrf = appliesInCrf;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }
}
