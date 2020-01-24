package eu.kvfg.refresh;

import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;

/**
 * @author Lukas Nasarek
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

    /**
     * Creates fake {@link GitProperties} in order to work without
     * maven (f.e. running refresh from Intellij).
     *
     * @return the pseudo git properties
     */
    @Bean
    @Profile("dev")
    public GitProperties devGitProperties() {
        Properties properties = new Properties();
        properties.setProperty("branch", "dev");
        // Hash of the first commit of this project.
        properties.setProperty("commit.id", "devdevdevdevdevdevdevdev");
        properties.setProperty("commit.time", "0");
        return new GitProperties(properties);
    }
}
