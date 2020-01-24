package eu.kvfg.refresh.security.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

import java.util.Collections;

/**
 * @author Lukas Nasarek
 */
@Configuration
public class AuthenticationProviderConfig {

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.dn.base}")
    private String baseDn;

    @Value("${ldap.dn.user-pattern}")
    private String userDnPattern;

    @Value("${ldap.group.search-base}")
    private String groupSearchBase;

    @Value("${ldap.group.search-filter}")
    private String groupSearchFilter;

    @Bean
    public BindAuthenticator configuredBindAuthenticator() {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(securityContextSource());
        bindAuthenticator.setUserSearch(
            new FilterBasedLdapUserSearch("", "uid={0}",
            securityContextSource()));
        bindAuthenticator.setUserDnPatterns(new String[]{userDnPattern});
        return bindAuthenticator;
    }

    @Bean
    public DefaultLdapAuthoritiesPopulator configuredAuthoritiesPopulator() {
        DefaultLdapAuthoritiesPopulator authoritiesPopulator
            = new DefaultLdapAuthoritiesPopulator(securityContextSource(),
            groupSearchBase);
        authoritiesPopulator.setGroupSearchFilter(groupSearchFilter);
        return authoritiesPopulator;
    }

    @Bean
    public DefaultSpringSecurityContextSource securityContextSource() {
        return new DefaultSpringSecurityContextSource(Collections.singletonList(url), baseDn);
    }
}
