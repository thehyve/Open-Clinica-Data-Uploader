package nl.thehyve.ocdu.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(targetEntity = EventDefinition.class)
    private List eventDefinitions;



}
