package eu.kvfg.refresh.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Lukas Nasarek
 */
public class LoginDisabledException extends AuthenticationException {

    public LoginDisabledException() {
        super("");
    }
}
