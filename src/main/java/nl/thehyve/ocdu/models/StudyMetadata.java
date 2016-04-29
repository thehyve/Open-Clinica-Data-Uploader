package nl.thehyve.ocdu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by piotrzakrzewski on 29/04/16.
 */
@Entity
public class StudyMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;



}
