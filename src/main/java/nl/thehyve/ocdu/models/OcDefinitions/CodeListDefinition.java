package nl.thehyve.ocdu.models.OcDefinitions;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 01/05/16.
 */
@Entity
public class CodeListDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String ocid;

    public String getOcid() {
        return ocid;
    }

    public void setOcid(String ocid) {
        this.ocid = ocid;
    }

    @OneToMany(targetEntity = CodeListItemDefinition.class)
    private List<CodeListItemDefinition> items = new ArrayList<>();

    public List<CodeListItemDefinition> getItems() {
        return items;
    }

    public void setItems(List<CodeListItemDefinition> items) {
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public boolean isAllowed(String value) {
        return items.stream()
                .anyMatch(codeListItemDefinition -> codeListItemDefinition.getContent().equals(value));
    }
}
