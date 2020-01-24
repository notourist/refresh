package eu.kvfg.refresh.weather.model;

import java.util.HashMap;

/**
 * @author Lukas Nasarek
 */
public enum WeatherDataType {

    TEMPERATURE,
    HUMIDITY,
    VOLTAGE;

    private static final HashMap<Integer, WeatherDataType> TYPE_LOOKUP = new HashMap<>();

    static {
        TYPE_LOOKUP.put(1, TEMPERATURE);
        TYPE_LOOKUP.put(2, HUMIDITY);
        TYPE_LOOKUP.put(3, VOLTAGE);
    }

    public static WeatherDataType of(int type) {
        return TYPE_LOOKUP.get(type);
    }
}
