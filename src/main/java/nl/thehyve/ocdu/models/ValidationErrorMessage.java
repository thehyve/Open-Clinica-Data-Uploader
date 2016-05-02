package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class ValidationErrorMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
}
