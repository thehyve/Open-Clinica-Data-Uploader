package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by bo on 6/8/16.
 */
@Entity
public class SiteDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String siteOID;

    private String name;

    private boolean genderRequired = true;
    //TODO: refactor birthdateRequired into Enumeration
    /*
     * 1. yes, required
     * 2. only year of birth
     * 3. not required
     */
    private int birthdateRequired = 1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getSiteOID() {
        return siteOID;
    }

    public void setSiteOID(String siteOID) {
        this.siteOID = siteOID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
