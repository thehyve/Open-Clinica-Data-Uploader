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

    private String studyIdentifier; //TODO: shouldn't it be Study entity? Should we serialize Study as well?

    @OneToMany(targetEntity = EventDefinition.class, cascade = CascadeType.ALL)
    private List eventDefinitions;

    @OneToMany(targetEntity = ItemGroupDefinition.class, cascade = CascadeType.ALL )
    private List itemGroupDefinitions;

    @OneToMany(targetEntity = CRFDefinition.class, cascade = CascadeType.ALL )
    private List crfDefinitions;

    @OneToMany(targetEntity = CodeListDefinition.class, cascade = CascadeType.ALL )
    private List codeListDefinitions;



}
