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
        if (matchesFilter(metaDataTree, selection)) {
            return metaDataTree.getChildren().stream()
                    .filter(childNode -> childNode.getName().equals(selection.getVersion())).findFirst().get();
        } else {
            for (MetaDataTree childNode: metaDataTree.getChildren()) {
                MetaDataTree matching = filter(childNode, selection);
                if (matching != null) {
                    return matching;
                }
            }
            return null;
        }
    }

    private static boolean matchesFilter(MetaDataTree node, OcTreePath selection) {
        if (node.getName().equals(selection.getCrf()) && node.getChildren().size() > 0  ) {
            boolean versionMatch = node.getChildren().stream().anyMatch(childNode -> childNode.getName().equals(selection.getVersion()));
            if (versionMatch) return true;
            else return false;
        } else {
            return false;
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
