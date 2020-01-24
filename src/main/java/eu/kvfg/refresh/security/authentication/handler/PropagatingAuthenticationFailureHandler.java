package eu.kvfg.refresh.security.authentication.handler;

import eu.kvfg.refresh.security.authentication.exception.LoginDeniedException;
import eu.kvfg.refresh.security.authentication.exception.LoginDisabledException;
import eu.kvfg.refresh.util.Utils;
import eu.kvfg.refresh.util.response.ResponseBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukas Nasarek
 */
@Component
public class PropagatingAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Map<Class<? extends AuthenticationException>, Integer> ERROR_CODES
        = new HashMap<>();

    static {
        ERROR_CODES.put(BadCredentialsException.class, 2);
        ERROR_CODES.put(LoginDisabledException.class, 3);
        ERROR_CODES.put(LoginDeniedException.class, 4);
    }

    private static Integer getError(Class<? extends AuthenticationException> exceptionClass) {
        return Utils.requireNonNullElse(ERROR_CODES.get(exceptionClass), -1);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        new ResponseBuilder()
            .unauthorized(request.getRequestURI(), getError(exception.getClass()))
            .writeTo(response);
    }
}
