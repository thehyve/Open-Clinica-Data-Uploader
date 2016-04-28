package nl.thehyve.ocdu;

/**
 * Created by piotrzakrzewski on 21/03/16.
 */


import nl.thehyve.ocdu.security.CustomPasswordEncoder;
import nl.thehyve.ocdu.security.ExUsernamePasswordAuthenticationFilter;
import nl.thehyve.ocdu.security.OcSOAPAuthenticationProvider;
import nl.thehyve.ocdu.security.OcUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);


    private AuthenticationFailureHandler authenticationFailureHandler = (request, response, e) -> {
        log.error("Error: " + e.getMessage());
        response.sendRedirect("/login?error");
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/data").authenticated()
                .and()
                .formLogin()
                .permitAll()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();


        http.csrf().disable();
        ExUsernamePasswordAuthenticationFilter customFilter = new ExUsernamePasswordAuthenticationFilter();
        customFilter.setAuthenticationManager(authenticationManager);
        customFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        http.addFilter(customFilter);
    }

    @Bean(name = "myAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
       /* auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");*/
        auth.authenticationProvider(ocSOAPAuthenticationProvider)
                .userDetailsService(ocUserDetailsService)
                .passwordEncoder(new CustomPasswordEncoder());
    }


    @Autowired
    OcSOAPAuthenticationProvider ocSOAPAuthenticationProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    OcUserDetailsService ocUserDetailsService;

}
