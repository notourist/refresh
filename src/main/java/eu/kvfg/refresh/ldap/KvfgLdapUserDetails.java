package eu.kvfg.refresh.ldap;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import java.util.Collection;

import static eu.kvfg.refresh.ldap.KvfgLdapRoles.*;

/**
 * Allows access to school specific data.
 *
 * @author Lukas Nasarek
 */
public class KvfgLdapUserDetails implements LdapUserDetails {

    /**
     * This class is serialized into the postgres and Spring started complaining about the uid.
     */
    public static final long serialVersionUID = 6809672266666498976L;

    /**
     * This object MUST be named ldapUserDetails else Spring gets fuc...
     * confused with it's Reflection magic.
     */
    private final LdapUserDetails ldapUserDetails;

    @Getter
    private final String displayName;

    @Getter
    private final String gradeLiteral;

    public KvfgLdapUserDetails(LdapUserDetails ldapUserDetails, String displayName, String gradeLiteral) {
        this.ldapUserDetails = ldapUserDetails;
        this.displayName = displayName;
        this.gradeLiteral = gradeLiteral;
    }

    public boolean isAdmin() {
        return ldapUserDetails.getAuthorities().contains(ADMIN.asAuthority());
    }

    public boolean isStudent() {
        return ldapUserDetails.getAuthorities().contains(STUDENT.asAuthority());
    }

    public boolean isTeacher() {
        return ldapUserDetails.getAuthorities().contains(TEACHER.asAuthority());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return ldapUserDetails.getAuthorities();
    }

    /**
     * Always return a blank String.
     * The UserDetails provided by LDAP return null for
     * {@link UserDetails#getPassword()}.
     * Spring doesn't like that and crashes.
     */
    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return ldapUserDetails.getUsername().toUpperCase();
    }

    @Override
    public boolean isAccountNonExpired() {
        return ldapUserDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return ldapUserDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return ldapUserDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return ldapUserDetails.isEnabled();
    }

    @Override
    public String getDn() {
        return ldapUserDetails.getDn();
    }

    @Override
    public void eraseCredentials() {
        ldapUserDetails.eraseCredentials();
    }
}