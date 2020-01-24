package eu.kvfg.refresh.logging.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/logging/exceptions")
public class ExceptionController {

    private final ExceptionLogger logger;

    @Autowired
    public ExceptionController(ExceptionLogger logger) {
        this.logger = logger;
    }

    @GetMapping
    public List<ExceptionInformation> exceptions() {
        return logger.getExceptions();
    }
}
