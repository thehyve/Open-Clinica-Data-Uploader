package nl.thehyve.ocdu.models.OCEntities;

/**
 * Created by piotrzakrzewski on 15/04/16.
 */
public class Study {

    private final String identifier;
    private final  String oid;
    private final String name;

    public Study(String identifier, String oid, String name) {
        this.identifier = identifier;
        this.oid = oid;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getOid() {
        return oid;
    }

    public String getName() {
        return name;
    }
}
