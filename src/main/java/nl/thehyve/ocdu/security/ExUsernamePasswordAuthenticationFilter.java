package nl.thehyve.ocdu.security;

import nl.thehyve.ocdu.OCEnvironmentsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter required to pass on user selected OcEnvironment during login.
 *
 * Created by piotrzakrzewski on 18/04/16.
 */
public class ExUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(ExUsernamePasswordAuthenticationFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String ocEnvironment = request.getParameter(OCEnvironmentsConfig.OC_ENV_ATTRIBUTE_NAME);
        log.info("Attempted authentication against: " + ocEnvironment);
        String password = request.getParameter("password");
        CustomPasswordEncoder encoder = new CustomPasswordEncoder();
        password = encoder.encode(password);
        request.getSession().setAttribute("ocwsHash", password);
        request.getSession().setAttribute(OCEnvironmentsConfig.OC_ENV_ATTRIBUTE_NAME, ocEnvironment);

        return super.attemptAuthentication(request, response);
    }

    public ExUsernamePasswordAuthenticationFilter() {
        super.setPostOnly(true);  //TODO: should be defined as a Bean
    }

}
