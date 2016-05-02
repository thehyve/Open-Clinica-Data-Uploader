package nl.thehyve.ocdu.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */

@Entity
public class OcUser{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String ocEnvironment;
    private String username;


    public OcUser() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public List getUploadSessions() {
        return uploadSessions;
    }

    public void setUploadSessions(List uploadSessions) {
        this.uploadSessions = uploadSessions;
    }
*/
    public String getOcEnvironment() {
        return ocEnvironment;
    }

    public void setOcEnvironment(String ocEnvironment) {
        this.ocEnvironment = ocEnvironment;
    }
}
