package nl.thehyve.ocdu;

/**
 * Created by piotrzakrzewski on 21/03/16.
 */


import nl.thehyve.ocdu.security.CustomPasswordEncoder;
import nl.thehyve.ocdu.security.ExUsernamePasswordAuthenticationFilter;
import nl.thehyve.ocdu.security.OcUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/data").authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();

        http.csrf().disable();
        ExUsernamePasswordAuthenticationFilter customFilter = new ExUsernamePasswordAuthenticationFilter();
        customFilter.setAuthenticationManager(authenticationManager);
        http.addFilter(customFilter);
    }

    @Bean(name="myAuthenticationManager")
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
