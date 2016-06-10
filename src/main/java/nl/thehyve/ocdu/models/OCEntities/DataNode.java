package nl.thehyve.ocdu.models.OCEntities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Not thread-safe.
 * Created by jacob on 6/10/16.
 */
public class DataNode {

    /*
     * Note: we do not include nodeSet in the equals and hash. We assume that the validation has signaled generated an
     * error for duplicate lines
     */
    private String name;
    private String value;
    private Set<DataNode> nodeSet = new HashSet<DataNode>();
    private List<DataNode> nodeList = new ArrayList<DataNode>();


    public DataNode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void addChild(DataNode child) {
        if (nodeSet.add(child)) {
            nodeList.add(child);
        }
    }

    public DataNode findChild(DataNode child) {
        if (nodeSet.contains(child)) {
            return nodeList.get(nodeList.indexOf(child));
        }
        throw new IllegalStateException("Node does not contain child " + child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataNode dataNode = (DataNode) o;

        if (name != null ? !name.equals(dataNode.name) : dataNode.name != null) return false;
        return value != null ? value.equals(dataNode.value) : dataNode.value == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public static void printAsTree(DataNode dataNode) {
        for (DataNode nodeToPrint : dataNode.nodeList) {
            System.out.println(nodeToPrint);

        }
        for (DataNode nodeToPrint : dataNode.nodeList) {
            printAsTree(nodeToPrint);
        }
    }




    @Override
    public String toString() {
        return name + "->" + value;
    }
}
