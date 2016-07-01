package nl.thehyve.ocdu.models;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jacob Rousseau on 01-Jul-2016.
 * Copyright CTMM-TraIT / NKI (c) 2016
 */
public enum CRFStatusAfterUpload {

    DATA_ENTRY_STARTED("initial data entry"), DATA_ENTRY_COMPLETED("complete");

    private String name;

    private static final Map<String, CRFStatusAfterUpload> ENUM_MAP;

    CRFStatusAfterUpload (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static {
        Map<String, CRFStatusAfterUpload> map = new ConcurrentHashMap<String, CRFStatusAfterUpload>();
        for (CRFStatusAfterUpload instance : CRFStatusAfterUpload.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static CRFStatusAfterUpload get (String name) {
        return ENUM_MAP.get(name);
    }
}
