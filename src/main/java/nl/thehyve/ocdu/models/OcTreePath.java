package nl.thehyve.ocdu.models;

import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;

/**
 * Created by piotrzakrzewski on 21/06/16.
 *
 * Represents selection on the OpenClinica Study tree. e.g. Study1->Event1->Crf1->ver1
 */
public class OcTreePath {

    private String study;
    private String event;
    private String crf;
    private String version;
    private String item;

    public static OcEntity filter(MetaData metaData, OcTreePath selection) {

    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCrf() {
        return crf;
    }

    public void setCrf(String crf) {
        this.crf = crf;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
