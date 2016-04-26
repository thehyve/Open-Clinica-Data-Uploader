package nl.thehyve.ocdu;

/**
 * Created by piotrzakrzewski on 21/03/16.
 */


import nl.thehyve.ocdu.security.ExUsernamePasswordAuthenticationFilter;
import nl.thehyve.ocdu.security.OcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/data").authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
//                .successHandler(successHandler())
                .permitAll()
                .and()
            .logout()
                .permitAll();

        http.csrf().disable();
    }


  /*  @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }*/

    @Autowired
    OcSOAPAuthenticationProvider ocSOAPAuthenticationProvider;

    @Autowired
    OcUserDetailsService ocUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ocSOAPAuthenticationProvider);
        auth.userDetailsService(ocUserDetailsService);
    }

//TODO: Configure custom UserDetails service
//TODO: Configure password encoder with sha1
//   @Bean
//    public AuthenticationSuccessHandler successHandler() {
//        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
//        handler.setUseReferer(true);
//        handler.setDefaultTargetUrl("/events");
//        return handler;
//    }


}
