package eu.kvfg.refresh.logging.exception;

import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

/**
 * @author Lukas Nasarek
 */
class ExceptionInformation {

    @Getter
    private final Instant timestamp;

    @Getter
    private final String message;

    @Getter
    private final String stackTrace;

    ExceptionInformation(Exception exception, Instant timestamp) {
        this.timestamp = timestamp;
        this.message = exception.getMessage();

        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        this.stackTrace = stringWriter.toString();
    }
}
