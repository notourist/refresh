package eu.kvfg.refresh.logging.request;

import eu.kvfg.refresh.ldap.KvfgLdapUserDetails;
import eu.kvfg.refresh.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Logs all request made to all the endpoints.
 * The information which is display can be changed
 * via {@code logging.request...}. This filter does
 * not catch login request.
 *
 * @author Lukas Nasarek
 */
@Component
public class RequestLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);

    private final List<RequestInformation> requestInformationList = new ArrayList<>();

    @Value("${logging.request.normal.responses}")
    private List<Integer> normalResponses;

    @Value("${logging.request.normal.process-time}")
    private int normalProcessTime;

    @Value("${logging.request.username}")
    private boolean logUsername;

    @Value("${logging.request.ip}")
    private boolean logIp;

    @Value("${logging.request.method}")
    private boolean logMethod;

    @Value("${logging.request.path}")
    private boolean logPath;

    @Value("${logging.request.parameters}")
    private boolean logParameters;

    @Value("${logging.request.cookies}")
    private boolean logCookies;

    @Scheduled(fixedRateString = "${logging.request.clear-interval}")
    public void clearRequestInfoList() {
        requestInformationList.clear();
    }

    /**
     * Returns all the requests where the response status is not 200 or
     * their process time is longer then 500 ms.
     *
     * @return all the requests where the response status is not 200 or
     * their process time is longer then 500 ms.
     */
    List<RequestInformation> getAbnormalRequests() {
        return requestInformationList
            .stream()
            .filter(this::isAbnormal)
            .collect(Collectors.toList());
    }

    Map<String, Integer> hitsPerUser() {
        final Map<String, Integer> hashMap = new HashMap<>();
        for (final RequestInformation requestInformation : requestInformationList) {
            int prevHits = hashMap.getOrDefault(requestInformation.getUsername(), 0);
            hashMap.put(requestInformation.getUsername(), ++prevHits);
        }
        return hashMap
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            // complicated sorting
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (e1, e2) -> e1, LinkedHashMap::new));
    }

    List<RequestInformation> getRequestInformation() {
        return requestInformationList;
    }

    void log(HttpServletRequest request, HttpServletResponse response, long processTime) {
        String username = "unauthenticated";

        if (logUsername) {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                Object principal = Utils.getAuthenticationPrincipal();
                if (principal instanceof KvfgLdapUserDetails) {
                    username = ((KvfgLdapUserDetails) principal).getUsername();
                }
            }
        }

        String ip = "";
        if (logIp) {
            ip = Utils.requireNonNullElse(request.getHeader("x-forwarded-for"),
                request.getRemoteAddr() + "(no header)");
        }

        String method = "";
        if (logMethod) {
            method = request.getMethod();
        }

        String path = "";
        if (logPath) {
            path = request.getRequestURI();
        }

        int responseStatus = response.getStatus();

        String parameters = "";
        if (logParameters) {
            if (request.getQueryString() != null
                    // Prevent password leakage
                    && !request.getQueryString().startsWith("password")) {
                parameters = request.getQueryString();
            }
        }

        Map<String, String> cookieMap = new HashMap<>();
        if (logCookies) {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    cookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
        }

        RequestInformation requestInformation = new RequestInformation(username, ip,
            Instant.ofEpochMilli(System.currentTimeMillis()),
            Duration.ofMillis(processTime), responseStatus,
            method, path, parameters, cookieMap);
        LOGGER.info("New request: {}", requestInformation.asLogString());

        requestInformationList.add(requestInformation);
    }

    private boolean isAbnormal(RequestInformation requestInformation) {
        return !normalResponses.contains(requestInformation.getResponseStatus())
            | requestInformation.getProcessTime().toMillis() >= normalProcessTime;
    }
}
