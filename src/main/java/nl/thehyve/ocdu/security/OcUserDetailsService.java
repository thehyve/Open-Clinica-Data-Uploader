package nl.thehyve.ocdu.security;

import nl.thehyve.ocdu.models.OcUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
@Service
public class OcUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(OcUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authList = new ArrayList<>();
        HttpSession session = session();
        log.debug("User retrieved: " + username );
        String ocEnvironment = (String) session.getAttribute("ocEnvironment");
        UserDetails usr = new OcUser(username, "notused", authList, ocEnvironment);
        return usr;
    }

    private HttpSession session() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true == allow create
    }
}
