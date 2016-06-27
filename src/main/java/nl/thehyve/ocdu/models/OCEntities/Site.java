package nl.thehyve.ocdu.models.OCEntities;

import nl.thehyve.ocdu.models.OcDefinitions.ODMElement;

/**
 * Created by Jacob Rousseau on 24-Jun-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public class Site extends AbstractStudySiteBase implements ODMElement {

    public Site(String identifier, String oid, String name) {
        super(identifier, oid, name);
    }


    @Override
    public String toString() {
        return "Site{" +
                "identifier='" + identifier + '\'' +
                ", oid='" + oid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
