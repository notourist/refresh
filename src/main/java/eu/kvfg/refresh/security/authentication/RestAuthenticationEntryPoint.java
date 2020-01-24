package eu.kvfg.refresh.security.authentication;

import eu.kvfg.refresh.util.response.ResponseBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lukas Nasarek
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // We do NOT want to send the www-authenticate header
        // because browsers will show an ugly authentication pop up.
        new ResponseBuilder()
            .unauthorized(request.getRequestURI())
            .writeTo(response);
    }
}