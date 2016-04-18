package nl.thehyve.ocdu;

import nl.thehyve.ocdu.security.OcUser;
import nl.thehyve.ocdu.services.OpenClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
@Component
public class OcSOAPAuthenticationProvider implements AuthenticationProvider{


    @Autowired
    OpenClinicaService openClinicaService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        OcUser usr = (OcUser) authentication.getDetails();
        String ocEnvironment = usr.getOcEnvironment();

        try {
            if (openClinicaService.isAuthenticated(name,password, ocEnvironment)) {
                List<GrantedAuthority> grantedAuths = new ArrayList<>();
                return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
            } else {
                throw new AuthenticationServiceException("Unable to auth against third party systems");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
