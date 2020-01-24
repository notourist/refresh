package eu.kvfg.refresh.weather.model;

import lombok.Getter;

import java.time.Instant;

/**
 * POJO for the database weather data model.
 *
 * @author Lukas Nasarek
 */
public class WeatherData {

    @Getter
    private final double value;

    @Getter
    private final Instant timestamp;

    @Getter
    private final WeatherDataType type;

    public WeatherData(double value, Instant timestamp, WeatherDataType type) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
    }
}
