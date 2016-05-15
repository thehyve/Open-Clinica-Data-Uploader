package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
@Entity
public class RangeCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private COMPARATOR comparator;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public COMPARATOR getComparator() {
        return comparator;
    }

    public void setComparator(COMPARATOR comparator) {
        this.comparator = comparator;
    }

    public enum COMPARATOR {
        LE, GE, LT, GT, NE, EQ
    }

    public boolean isInRange(int comparedValue) {
        if (comparator == COMPARATOR.GE) {
            return comparedValue >= value;
        } else if (comparator == COMPARATOR.GT) {
            return comparedValue > value;
        } else if (comparator == COMPARATOR.LE) {
            return comparedValue <= value;
        } else if (comparator == COMPARATOR.LT) {
            return comparedValue < value;
        } else if (comparator == COMPARATOR.EQ) {
            return comparedValue == value;
        } else if (comparator == COMPARATOR.NE) {
            return comparedValue != value;
        } else {
            return false;
        }
    }

}
