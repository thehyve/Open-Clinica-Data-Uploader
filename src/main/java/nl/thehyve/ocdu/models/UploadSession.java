package nl.thehyve.ocdu.models;

import javax.persistence.*;

/**
 * Created by piotrzakrzewski on 28/04/16.
 */
@Entity
public class UploadSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @ManyToOne
    private OcUser owner;

    private String lastStep;

}
