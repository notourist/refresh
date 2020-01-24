package eu.kvfg.refresh.mensa;

import eu.kvfg.refresh.util.cache.CacheResponse;
import lombok.Getter;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

class MensaResponse extends CacheResponse<DailyMenu> {

    @Getter
    private final Map<Integer, String> notes;

    MensaResponse(Collection<DailyMenu> data, Instant lastSuccessfulUpdate, Map<Integer, String> notes) {
        super(data, lastSuccessfulUpdate);
        this.notes = notes;
    }
}
