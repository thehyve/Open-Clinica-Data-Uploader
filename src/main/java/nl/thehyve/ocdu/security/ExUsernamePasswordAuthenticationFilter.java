package nl.thehyve.ocdu.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

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
/*
    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }*/
}
