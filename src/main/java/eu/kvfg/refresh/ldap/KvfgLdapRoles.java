package eu.kvfg.refresh.ldap;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Wraps the the LDAP roles used for authentication users in an enum.
 * This makes refactoring code easier.
 */
public enum KvfgLdapRoles {

    STUDENT("ROLE_SCHUELER"),
    ADMIN("ROLE_ADMIN"),
    TEACHER("ROLE_LEHRER");

    private final String role;

    KvfgLdapRoles(String role) {
        this.role = role;
    }

    public GrantedAuthority asAuthority() {
        return new SimpleGrantedAuthority(role);
    }
}
