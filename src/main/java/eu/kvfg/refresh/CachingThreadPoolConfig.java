package eu.kvfg.refresh;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * @author Lukas Nasarek
 */
@Component
public class CachingThreadPoolConfig {

    @Bean("cacheRefreshPool")
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("caching-pool");
        return threadPoolTaskScheduler;
    }

}
