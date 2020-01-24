package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.weather.model.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author Lukas Nasarek
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class StationDao {

    private static final String LIST_STATIONS
        = "SELECT * FROM station";

    private static final String STATION_BY_ID
        = "SELECT * FROM station WHERE id = :id";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    private final StationRowMapper stationRowMapper;

    @Autowired
    public StationDao(@Qualifier("weatherTemplate") NamedParameterJdbcTemplate namedJdbcTemplate,
                      StationRowMapper stationRowMapper) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.stationRowMapper = stationRowMapper;
    }

    List<Station> listStations() {
        return namedJdbcTemplate.query(LIST_STATIONS, stationRowMapper);
    }

    Station stationById(long id) {
        return namedJdbcTemplate
            .queryForObject(STATION_BY_ID, Collections.singletonMap("id", id), stationRowMapper);
    }
}