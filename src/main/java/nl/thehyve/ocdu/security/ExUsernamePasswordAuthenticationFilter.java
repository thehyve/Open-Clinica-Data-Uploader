package nl.thehyve.ocdu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by piotrzakrzewski on 18/04/16.
 */
public class ExUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String ocEnvironment = request.getParameter("ocEnvironment");
        request.getSession().setAttribute("ocEnvironment", ocEnvironment);

        return super.attemptAuthentication(request, response);
    }
}
