package eu.kvfg.refresh.weather.model;

import lombok.Getter;

import java.time.Instant;

/**
 * POJO for the database station model.
 *
 * @author Lukas Nasarek
 */
public class Station {

    @Getter
    private final long id;

    @Getter
    private final String location;

    @Getter
    private final int hwRev;

    @Getter
    private final int swRev;

    @Getter
    private final int status;

    @Getter
    private final int proto;

    @Getter
    private final int dup;

    @Getter
    private final Instant lastExchange;

    @Getter
    private final long lastSequence;

    public Station(long id, String location, int hwRev,
                   int swRev, int status, int proto,
                   int dup, Instant lastExchange, long lastSequence) {
        this.id = id;
        this.location = location;
        this.hwRev = hwRev;
        this.swRev = swRev;
        this.status = status;
        this.proto = proto;
        this.dup = dup;
        this.lastExchange = lastExchange;
        this.lastSequence = lastSequence;
    }
}