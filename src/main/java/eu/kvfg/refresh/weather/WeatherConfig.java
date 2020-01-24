package eu.kvfg.refresh.weather;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * @author Lukas Nasarek
 */
@Configuration
public class WeatherConfig {

    @Bean("weatherDataSource")
    @ConfigurationProperties(prefix = "weather.datasource")
    public DataSource weatherDataSource() {
        return DataSourceBuilder
            .create()
            .build();
    }

    @Bean("weatherTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(
        @Qualifier("weatherDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}