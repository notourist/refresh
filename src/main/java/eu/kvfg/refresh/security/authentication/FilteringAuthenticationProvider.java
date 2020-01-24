package eu.kvfg.refresh.security.authentication;

import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import eu.kvfg.refresh.security.KvfgLdapUserDetailsMapper;
import eu.kvfg.refresh.security.authentication.exception.LoginDeniedException;
import eu.kvfg.refresh.security.authentication.exception.LoginDisabledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author Lukas Nasarek
 */
@Component
public class FilteringAuthenticationProvider extends LdapAuthenticationProvider {

    @Value("${security.login.disabled}")
    private boolean loginDisabled;

    @Value("#{'${security.admins}'.split(',')}")
    private List<String> admins;

    @Value("#{'${security.login.denied.users}'.split(',')}")
    private List<String> usersLoginDenied;

    @Value("#{'${security.login.denied.grades}'.split(',')}")
    private List<String> gradesLoginDenied;

    @Value("#{'${security.login.allowed.users}'.split(',')}")
    private List<String> usersLoginAllowed;

    @Value("#{'${security.login.allowed.grades}'.split(',')}")
    private List<String> gradesLoginAllowed;

    @Autowired
    public FilteringAuthenticationProvider(LdapAuthenticator authenticator,
                                           LdapAuthoritiesPopulator authoritiesPopulator,
                                           KvfgLdapUserDetailsMapper ldapUserDetailsMapper) {
        super(authenticator, authoritiesPopulator);
        setUserDetailsContextMapper(ldapUserDetailsMapper);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
            messages.getMessage("LdapAuthenticationProvider.onlySupports",
                "Only UsernamePasswordAuthenticationToken is supported"));

        final UsernamePasswordAuthenticationToken userToken =
            (UsernamePasswordAuthenticationToken) authentication;

        String username = userToken.getName();
        String password = (String) authentication.getCredentials();

        if (logger.isDebugEnabled()) {
            logger.debug("Processing authentication request for user: " + username);
        }

        if (!StringUtils.hasLength(username)) {
            throw new BadCredentialsException(messages.getMessage(
                "LdapAuthenticationProvider.emptyUsername", "Empty Username"));
        }

        if (!StringUtils.hasLength(password)) {
            throw new BadCredentialsException(messages.getMessage(
                "AbstractLdapAuthenticationProvider.emptyPassword",
                "Empty Password"));
        }

        Assert.notNull(password, "Null password was supplied in authentication token");

        DirContextOperations userData = doAuthentication(userToken);

        Collection<? extends GrantedAuthority> userAuthorities = loadUserAuthorities(userData,
            authentication.getName(), (String) authentication.getCredentials());

        KvfgLdapUserDetails ldapUserDetails =
            (KvfgLdapUserDetails) userDetailsContextMapper.mapUserFromContext(userData,
                authentication.getName(), userAuthorities);

        if (usersLoginDenied.contains(username)
            || gradesLoginDenied.contains(ldapUserDetails.getGradeLiteral())) {
            throw new LoginDeniedException();
        } else if (loginDisabled) {
            if (!(admins.contains(username)
                || usersLoginAllowed.contains(username)
                || (ldapUserDetails.isStudent() && gradesLoginAllowed.contains(ldapUserDetails.getGradeLiteral())))) {
                throw new LoginDisabledException();
            }
        }
        return createSuccessfulAuthentication(userToken, ldapUserDetails);
    }
}
