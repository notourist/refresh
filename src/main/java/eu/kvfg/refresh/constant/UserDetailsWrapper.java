package eu.kvfg.refresh.constant;

import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import lombok.Getter;

/**
 * Data wrapper for the InternalInformationController.
 * This protects internal configuration details such as authorities.
 *
 * @author Lukas Nasarek
 */
class UserDetailsWrapper {

    @Getter
    private final String username;

    @Getter
    private final String displayName;

    @Getter
    private final String grade;

    @Getter
    private final boolean admin;

    @Getter
    private final boolean student;

    @Getter
    private final boolean teacher;

    private UserDetailsWrapper(String username, String displayName, String grade,
                               boolean admin, boolean student, boolean teacher) {
        this.username = username;
        this.displayName = displayName;
        this.grade = grade;
        this.admin = admin;
        this.student = student;
        this.teacher = teacher;
    }

    static UserDetailsWrapper ofLdapDetails(KvfgLdapUserDetails ldapUserDetails) {
        return new UserDetailsWrapper(ldapUserDetails.getUsername(),
            ldapUserDetails.getDisplayName(),
            ldapUserDetails.getGradeLiteral(),
            ldapUserDetails.isAdmin(),
            ldapUserDetails.isStudent(),
            ldapUserDetails.isTeacher());
    }

    static UserDetailsWrapper ofOther(Object principal) {
        return new UserDetailsWrapper(principal.toString(), "", "", false, false,
            false);
    }
}
