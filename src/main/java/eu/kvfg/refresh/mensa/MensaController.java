package eu.kvfg.refresh.mensa;

import eu.kvfg.refresh.util.cache.CacheController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lukas Nasarek
 */
@RestController
@RequestMapping("/mensa")
public class MensaController implements CacheController {

    private final MensaCache cache;

    @Autowired
    public MensaController(MensaCache cache) {
        this.cache = cache;
    }

    @GetMapping
    public MensaResponse menus() {
        return new MensaResponse(cache.getCached(),
            cache.getLastSuccessfulUpdate(),
            cache.getNotes());
    }
}
