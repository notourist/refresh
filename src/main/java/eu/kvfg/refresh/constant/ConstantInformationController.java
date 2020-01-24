package eu.kvfg.refresh.constant;

import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import eu.kvfg.refresh.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides access to constant information.
 * Doesn't provide any method of changing them.
 *
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/constant")
public class ConstantInformationController {

    private final VersionInformation versionInformation;

    @Autowired
    public ConstantInformationController(GitProperties gitProperties) {
        this.versionInformation = VersionInformation.of(gitProperties);
    }

    @GetMapping("/user")
    public UserDetailsWrapper ldapUserDetails() {
        Object principal = Utils.getAuthenticationPrincipal();
        if (principal instanceof KvfgLdapUserDetails) {
            return UserDetailsWrapper.ofLdapDetails((KvfgLdapUserDetails) principal);
        } else {
            return UserDetailsWrapper.ofOther(principal);
        }
    }

    @GetMapping("/version")
    public VersionInformation versionInformation() {
        return versionInformation;
    }
}