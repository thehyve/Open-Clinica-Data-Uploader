package nl.thehyve.ocdu.models;

import nl.thehyve.ocdu.services.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 21/06/16.
 */
public class MetaDataTree {
    private String name;
    //private MetaDataTree parent;
    private List<MetaDataTree> children = new ArrayList<>();

    public MetaDataTree(String name) {
        this.name = name;
    }

    public MetaDataTree() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MetaDataTree> getChildren() {
        return children;
    }

    public void setChildren(List<MetaDataTree> children) {
        this.children = children;
    }

    public void addChild(MetaDataTree node) {
        this.children.add(node);
    }
}
