package nl.thehyve.ocdu.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
public class OcUser extends User {

    private String ocEnvironment;

    public String getOcEnvironment() {
        return ocEnvironment;
    }

    public void setOcEnvironment(String ocEnvironment) {
        this.ocEnvironment = ocEnvironment;
    }

    public OcUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public OcUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                  String ocEnvironment) {
        super(username, password, authorities);
        this.ocEnvironment = ocEnvironment;
    }

    public OcUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
}
