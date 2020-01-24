package eu.kvfg.refresh.util.cache;

import lombok.Getter;

import java.time.Instant;
import java.util.Collection;

public class CacheResponse<T> {

    @Getter
    private final Collection<T> data;

    @Getter
    private final Instant lastSuccessfulUpdate;

    public CacheResponse(Collection<T> data, Instant lastSuccessfulUpdate) {
        this.data = data;
        this.lastSuccessfulUpdate = lastSuccessfulUpdate;
    }
}
