package eu.kvfg.refresh.logging.exception;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Nasarek
 */
@Component
public class ExceptionLogger {

    @Getter
    private final List<ExceptionInformation> exceptions = new ArrayList<>();

    public void addException(Exception exception) {
        ExceptionInformation exceptionInformation
            = new ExceptionInformation(exception, Instant.ofEpochMilli(System.currentTimeMillis()));
        exceptions.add(exceptionInformation);
    }
}
