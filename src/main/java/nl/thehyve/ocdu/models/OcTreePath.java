package nl.thehyve.ocdu.models;

import nl.thehyve.ocdu.models.OCEntities.OcEntity;
import nl.thehyve.ocdu.models.OcDefinitions.EventDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.ItemDefinition;
import nl.thehyve.ocdu.models.OcDefinitions.MetaData;
import nl.thehyve.ocdu.services.DataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 21/06/16.
 * <p>
 * Represents selection on the OpenClinica Study tree. e.g. Study1->Event1->Crf1->ver1
 */
public class OcTreePath {

    private String crf;
    private String version;
    private String event; //TODO: extend to support selection on event

    /**
     * @param metaDataTree
     * @param selection
     * @return null if no node matches selection, single node if it does
     */
    public static MetaDataTree filter(MetaDataTree metaDataTree, OcTreePath selection) {
        if (metaDataTree.getName().equals(selection.getCrf())) {
            for (MetaDataTree node : metaDataTree.getChildren()) {
                if (node.getName().equals(selection.getVersion())) {
                    return node;
                }
            }
            return null;
        } else {
            for (MetaDataTree node : metaDataTree.getChildren()) {
                MetaDataTree found = filter(node, selection);
                if (node != null) return found;
            }
            return null;
        }
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

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
