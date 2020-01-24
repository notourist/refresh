package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.weather.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Nasarek
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class WeatherDao {

    private static final String ALL_DATA_BY_ID
        = "SELECT * FROM data WHERE station = :id ORDER BY t ASC";

    private static final String LIMITED_DATA_BY_ID
        = "SELECT * FROM data WHERE station = :id AND t > :begin AND t < :end AND stype = :type ORDER BY t ASC";

    private static final String LAST_DATA_BY_ID
        = "SELECT * FROM data WHERE station = :id ORDER BY t DESC FETCH FIRST 3 ROWS ONLY";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private final WeatherRowMapper weatherRowMapper;

    @Autowired
    public WeatherDao(@Qualifier("weatherTemplate") NamedParameterJdbcTemplate namedJdbcTemplate,
                      WeatherRowMapper weatherRowMapper) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.weatherRowMapper = weatherRowMapper;
    }

    List<WeatherData> allDataById(long id) {
        return namedJdbcTemplate.query(ALL_DATA_BY_ID, Collections.singletonMap("id", id), weatherRowMapper);
    }

    List<WeatherData> limitedDataById(long id, long begin, long end, int count, int type) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("begin", new Timestamp(begin));
        params.put("end", new Timestamp(end));
        params.put("count", count);
        params.put("type", type);

        return namedJdbcTemplate.query(LIMITED_DATA_BY_ID, params, weatherRowMapper);
    }

    List<WeatherData> lastDataById(long id) {
        return namedJdbcTemplate.query(LAST_DATA_BY_ID, Collections.singletonMap("id", id), weatherRowMapper);
    }
}
