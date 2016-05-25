package nl.thehyve.ocdu.security;

import nl.thehyve.ocdu.models.OcUser;
import nl.thehyve.ocdu.models.OcUserDetails;
import nl.thehyve.ocdu.repositories.OCUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
@Service
public class OcUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(OcUserDetailsService.class);

    @Autowired
    OCUserRepository OCUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authList = new ArrayList<>();
        HttpSession session = session();
        List<OcUser> byUsername = OCUserRepository.findByUsername(username);
        String ocEnvironment = (String) session.getAttribute("ocEnvironment");
        List<OcUser> matching = byUsername.stream().filter(usr -> usr.getOcEnvironment().equals(ocEnvironment)).collect(Collectors.toList());
        if (matching.size() > 1) {
            log.error("More than 1 user with the same username and ocEnvironment"); // TODO: make sure this is the proper way
            return null;
        } else if (matching.size() == 1) {
            log.info("User retrieved from the local database: " + username+ " on OC env: "+ ocEnvironment );
            return getDetails(matching.get(0), authList);
        } else {
            log.info("User: " + username+ " on OC env: "+ ocEnvironment +" logged in for the first time. OcUser entity created and saved to the local database." );
            OcUser usr = new OcUser();
            usr.setOcEnvironment(ocEnvironment);
            usr.setUsername(username);
            OCUserRepository.save(usr);
            return getDetails(usr, authList);
        }
    }

    private OcUserDetails getDetails(OcUser ocUser, List<GrantedAuthority> auth) {
        OcUserDetails details = new OcUserDetails(ocUser.getUsername(),"not used", auth);
        details.setOcEnvironment(ocUser.getOcEnvironment());
        return details;
    }

    private HttpSession session() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true == allow create
    }
}
