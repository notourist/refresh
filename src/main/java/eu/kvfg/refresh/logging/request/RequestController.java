package eu.kvfg.refresh.logging.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/logging/requests")
public class RequestController {

    private final RequestLogger requestLogger;

    @Autowired
    public RequestController(RequestLogger requestLogger) {
        this.requestLogger = requestLogger;
    }

    @GetMapping
    public List<RequestInformation> requests() {
        return requestLogger.getRequestInformation();
    }

    @GetMapping("/abnormal")
    public List<RequestInformation> abnormalRequests() {
        return requestLogger.getAbnormalRequests();
    }

    @GetMapping("/hits")
    public Map<String, Integer> hitsPerUser() {
        return requestLogger.hitsPerUser();
    }
}
