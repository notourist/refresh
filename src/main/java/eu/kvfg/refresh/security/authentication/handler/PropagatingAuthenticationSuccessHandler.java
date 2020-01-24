package eu.kvfg.refresh.security.authentication.handler;

import eu.kvfg.refresh.util.response.ResponseBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Does not redirect after a successful authentication.
 *
 * @author Lukas Nasarek
 */
@Component
public class PropagatingAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        new ResponseBuilder()
            .ok(request.getRequestURI())
            .writeTo(response);

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }

        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || targetUrlParam != null && !"".equals(request.getParameter(targetUrlParam))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }
}