package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.util.Utils;
import eu.kvfg.refresh.weather.model.Station;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lukas Nasarek
 */
@Component
public class StationRowMapper implements RowMapper<Station> {

    public Station mapRow(ResultSet resultSet, int row) throws SQLException {
        return new Station(resultSet.getLong("id"),
            Utils.requireNonNullElse(resultSet.getString("location"), ""),
            resultSet.getInt("hwRev"),
            resultSet.getInt("swRev"),
            resultSet.getInt("status"),
            resultSet.getInt("proto"),
            resultSet.getInt("dup"),
            resultSet.getTimestamp("last").toInstant(),
            resultSet.getLong("lastseq"));
    }
}
