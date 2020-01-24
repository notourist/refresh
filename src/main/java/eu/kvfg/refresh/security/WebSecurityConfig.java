package eu.kvfg.refresh.security;

import eu.kvfg.refresh.security.authentication.FilteringAuthenticationProvider;
import eu.kvfg.refresh.security.authentication.RestAuthenticationEntryPoint;
import eu.kvfg.refresh.security.authentication.handler.PropagatingAuthenticationFailureHandler;
import eu.kvfg.refresh.security.authentication.handler.PropagatingAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.util.Arrays;

/**
 * Controls who can access what, provides the
 * {@link org.springframework.ldap.core.ContextSource}, etc.
 * Simply everything needed to secure refresh.
 *
 * @author Lukas Nasarek
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    private final RestAuthenticationEntryPoint entryPoint;

    private final PropagatingAuthenticationSuccessHandler authenticationSuccessHandler;

    private final PropagatingAuthenticationFailureHandler authenticationFailureHandler;

    private final FilteringAuthenticationProvider authenticationProvider;

    @Value("${spring.session.cookie.name}")
    private String cookieName;

    @Autowired
    public WebSecurityConfig(Environment environment, RestAuthenticationEntryPoint entryPoint,
                             PropagatingAuthenticationSuccessHandler authenticationSuccessHandler,
                             PropagatingAuthenticationFailureHandler authenticationFailureHandler,
                             FilteringAuthenticationProvider authenticationProvider) {
        this.environment = environment;
        this.entryPoint = entryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationProvider = authenticationProvider;
    }

    public void configure(HttpSecurity httpSecurity) throws Exception {
        // Enable cors related functionality if needed
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            httpSecurity
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .and()
                .cors();
        }

        httpSecurity
            .csrf().disable()
            .authorizeRequests()
            // Weather data and mensa menus don't need security, as
            // they don't contain private information
            .antMatchers("/station/**", "/mensa").permitAll()
            // Deny any unauthenticated request
            .anyRequest().fullyAuthenticated()
            .and()
            .formLogin()
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .and()
            .logout()
            .deleteCookies(cookieName)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(entryPoint);
    }

    public void configure(AuthenticationManagerBuilder auth) {
        auth
            .authenticationProvider(authenticationProvider);
    }
}
