package eu.kvfg.refresh.logging.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Lukas Nasarek
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter implements Ordered {

    private final RequestLogger requestLogger;

    @Autowired
    public RequestLoggingFilter(RequestLogger requestLogger) {
        this.requestLogger = requestLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        requestLogger.log(request, response, System.currentTimeMillis() - startTime);
    }

    @Override
    public void destroy() {
    }

    /**
     * Low filter order to catch all the requests.
     *
     * @return -
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
