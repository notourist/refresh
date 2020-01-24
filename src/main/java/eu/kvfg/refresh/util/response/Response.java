package eu.kvfg.refresh.util.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;

import java.time.Instant;

public class Response {

    @Getter
    private final Instant timestamp;

    @Getter
    private final int status;

    @Getter
    private final String path;

    @Getter
    private final int code;

    Response(Instant timestamp, int status, String path, int code) {
        this.timestamp = timestamp;
        this.status = status;
        this.path = path;
        this.code = code;
    }

    @SuppressWarnings("unused")
    @JsonGetter("timestamp")
    public String getJsonTimestamp() {
        return timestamp.toString();
    }
}
