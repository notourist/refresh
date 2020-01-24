package eu.kvfg.refresh.security;

import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.kvfg.refresh.ldap.KvfgLdapRoles.ADMIN;
import static eu.kvfg.refresh.ldap.KvfgLdapRoles.STUDENT;

/**
 * Custom admin role injection.
 *
 * @author Lukas Nasarek
 */
@Component
public class KvfgLdapUserDetailsMapper extends LdapUserDetailsMapper {

    @Value("#{'${security.admins}'.split(',')}")
    private List<String> admins;

    private static boolean isLdapGrade(String ldapAuthority) {
        return ldapAuthority.matches("ROLE_[\\d]{1,2}[\\D]") ||
            ldapAuthority.matches("ROLE_JG[\\d][\\D]");
    }

    private static String fromLdapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (!authorities.contains(STUDENT.asAuthority())) {
            // The user is a teacher, so he has no real class.
            return "?";
        }

        List<String> filteredAuthorities = authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .filter(KvfgLdapUserDetailsMapper::isLdapGrade)
            .collect(Collectors.toList());

        if (filteredAuthorities.size() != 1) {
            throw new IllegalArgumentException("Authorities don't contain grade. " +
                "Filtered authorities: " + filteredAuthorities);
        }

        String grade = filteredAuthorities.get(0).replace("ROLE_", "");

        // Remove tailing literals for senior grades.
        if (grade.matches("JG[\\d][\\D]")) {
            grade = grade.substring(0, 3);
        }
        return grade;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mutableAuthorities = new HashSet<>(authorities);
        if (admins.contains(username)) {
            mutableAuthorities.add(ADMIN.asAuthority());
        }

        String grade = fromLdapAuthorities(authorities);

        LdapUserDetails details = (LdapUserDetails) super.mapUserFromContext(ctx, username,
            mutableAuthorities);
        return new KvfgLdapUserDetails(details,
            ctx.getStringAttribute("cn"),
            grade);
    }
}
