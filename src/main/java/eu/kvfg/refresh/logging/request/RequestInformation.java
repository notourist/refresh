package eu.kvfg.refresh.logging.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * A simple request wrapper containing logging worthy information.
 *
 * @author Lukas Nasarek
 */
class RequestInformation {

    /**
     * The name of logged in user. Else "unauthenticated".
     */
    @Getter
    private final String username;

    /**
     * The IP which sent the request. If the IP is not set via a header, the String equals "X.X.X.X(no header)".
     */
    @Getter
    private final String ip;

    /**
     * The date the request was received.
     */
    @Getter
    private final Instant timestamp;

    /**
     * The time it took to process the request.
     */
    @Getter
    private final Duration processTime;

    /**
     * The HTTP status that was returned to the client.
     */
    @Getter
    private final int responseStatus;

    /**
     * The method that was used in all caps, e.g. "GET" or "POST".
     */
    @Getter
    private final String method;

    /**
     * The path of the request relative to the root path.
     */
    @Getter
    private final String path;

    /**
     * Parameters which where sent with the request.
     * <p><b>Turned off by default! See config file for additional information!</b></p>.
     */
    @Getter
    private final String parameters;

    /**
     * Cookies which where delivered with the request.
     * <p><b>Turned off by default! See config file for additional information!</b></p>.
     */
    @Getter
    private final Map<String, String> cookies;

    RequestInformation(String username, String ip, Instant timestamp, Duration processTime,
                       int responseStatus, String method, String path, String parameters,
                       Map<String, String> cookies) {
        this.username = username;
        this.ip = ip;
        this.timestamp = timestamp;
        this.processTime = processTime;
        this.responseStatus = responseStatus;
        this.method = method;
        this.path = path;
        this.parameters = parameters;
        this.cookies = cookies;
    }

    @SuppressWarnings("unused")
    @JsonGetter("processTime")
    long getJsonProcessTime() {
        return processTime.toMillis();
    }

    /**
     * This method omits the timestamp as it is already printed by the logger.
     *
     * @return the request information without the timestamp
     */
    String asLogString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("username", username)
            .append("ip", ip)
            .append("processTime", processTime)
            .append("responseStatus", responseStatus)
            .append("method", method)
            .append("path", path)
            .append("parameters", parameters)
            .append("cookies", cookies)
            .toString();
    }
}
