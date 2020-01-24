package eu.kvfg.refresh.security.session;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Configuration for the Postgres-backend which stores the sessions.
 *
 * @author Lukas Nasarek
 */
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 7776000)
public class JdbcSessionConfig {

    @Profile("default")
    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.session.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder
            .create()
            .build();
    }

    @Profile("dev")
    @Primary
    @Bean
    public EmbeddedDatabase embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("org/springframework/session/jdbc/schema-h2.sql")
            .build();
    }

    @Profile("dev")
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
