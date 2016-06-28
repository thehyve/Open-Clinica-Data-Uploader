package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * Created by piotrzakrzewski on 15/05/16.
 */
@Entity
public class RangeCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private COMPARATOR comparator;
    private BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
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

    public boolean isInRange(BigDecimal comparedValue) {
        if (comparator == COMPARATOR.GE) {
            return comparedValue.compareTo(value) >= 0;//comparedValue >= value;
        } else if (comparator == COMPARATOR.GT) {
            return comparedValue.compareTo(value) > 0;
        } else if (comparator == COMPARATOR.LE) {
            return comparedValue.compareTo(value) <= 0;
        } else if (comparator == COMPARATOR.LT) {
            return comparedValue.compareTo(value) < 0;
        } else if (comparator == COMPARATOR.EQ) {
            return comparedValue.compareTo(value) == 0;
        } else if (comparator == COMPARATOR.NE) {
            return comparedValue.compareTo(value) != 0;
        } else {
            return false;
        }
    }

    public String violationMessage() {
        String comparatorHumanReadable = getHumanReadableComparator();
        String message = "Should be "+ comparatorHumanReadable + value;
        return message;
    }

    private String getHumanReadableComparator() {
        if (comparator == COMPARATOR.GE) {
            return "greater than or equal to ";
        } else if (comparator == COMPARATOR.GT) {
            return "greater than ";
        } else if (comparator == COMPARATOR.LE) {
            return "lesser than or equal to ";
        } else if (comparator == COMPARATOR.LT) {
            return "lesser than ";
        } else if (comparator == COMPARATOR.EQ) {
            return "equal to ";
        } else if (comparator == COMPARATOR.NE) {
            return "not equal to ";
        } else {
            return "";
        }
    }
}
