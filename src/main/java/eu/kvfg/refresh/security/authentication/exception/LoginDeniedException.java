package eu.kvfg.refresh.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Lukas Nasarek
 */
public class LoginDeniedException extends AuthenticationException {

    public LoginDeniedException() {
        super("");
    }
}