package nl.thehyve.ocdu.models.OCEntities;

/**
 * Created by Jacob Rousseau on 24-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public abstract class AbstractStudySiteBase {

    protected final String identifier;
    protected final  String oid;
    protected final String name;

    public AbstractStudySiteBase(String identifier, String oid, String name) {
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
