package nl.thehyve.ocdu.models;

import javax.persistence.*;

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



}
