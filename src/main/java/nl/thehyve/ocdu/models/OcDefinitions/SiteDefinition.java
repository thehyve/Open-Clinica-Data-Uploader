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

    private boolean isGenderRequired = true;

    private boolean isBirthdateRequired = true;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isGenderRequired() {
        return isGenderRequired;
    }

    public void setGenderRequired(boolean genderRequired) {
        isGenderRequired = genderRequired;
    }

    public boolean isBirthdateRequired() {
        return isBirthdateRequired;
    }

    public void setBirthdateRequired(boolean birthdateRequired) {
        isBirthdateRequired = birthdateRequired;
    }
}
