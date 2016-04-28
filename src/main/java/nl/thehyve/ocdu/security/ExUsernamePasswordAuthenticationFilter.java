package nl.thehyve.ocdu.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
public class ExUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ExUsernamePasswordAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String ocEnvironment = request.getParameter("ocEnvironment");
        log.error("attemptAuthentication called in custom UsrPassAuthFilter. Passed ocEnv: "+ ocEnvironment);
        request.getSession().setAttribute("ocEnvironment", ocEnvironment);
        return super.attemptAuthentication(request, response);
    }

    public ExUsernamePasswordAuthenticationFilter() {
        super.setPostOnly(true);  //TODO: should be defined as a Bean
    }

}
