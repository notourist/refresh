package eu.kvfg.refresh.weather.data;

import eu.kvfg.refresh.weather.model.WeatherData;
import eu.kvfg.refresh.weather.model.WeatherDataType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Lukas Nasarek
 */
@Component
public class WeatherRowMapper implements RowMapper<WeatherData> {

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(3, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public WeatherData mapRow(ResultSet resultSet, int row) throws SQLException {
        return new WeatherData(
            round(resultSet.getDouble("svalue")),
            resultSet.getTimestamp("t").toInstant(),
            WeatherDataType.of(resultSet.getInt("stype")));
    }
}
