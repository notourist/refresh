package eu.kvfg.refresh.util.cache;

import eu.kvfg.refresh.util.CheckedSupplier;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This is an abstract cache. Deals with scheduling and exception handling.
 *
 * @param <T> The type the cache contains.
 * @author Lukas Nasarek
 */
public abstract class Cache<T> {

    @Getter
    protected final Collection<T> cached = new ArrayList<>();

    private final ThreadPoolTaskScheduler taskScheduler;

    private final long fixedRate;

    private final Logger logger;

    @Getter
    private Instant lastSuccessfulUpdate = Instant.MIN;

    protected Cache(ThreadPoolTaskScheduler taskScheduler, Class<? extends Cache<T>> derived, long fixedRate) {
        this.taskScheduler = taskScheduler;
        this.logger = LoggerFactory.getLogger(derived);
        this.fixedRate = fixedRate;
    }

    protected void start(CheckedSupplier<Collection<T>> checkedSupplier) {
        taskScheduler.scheduleAtFixedRate(new RefreshRunnable(checkedSupplier), fixedRate);
    }

    /**
     * This is the main part, where the derived caches' specified update method
     * is called via {@link CheckedSupplier#get()}.
     */
    private final class RefreshRunnable implements Runnable {

        private final CheckedSupplier<Collection<T>> checkedSupplier;

        private RefreshRunnable(CheckedSupplier<Collection<T>> checkedSupplier) {
            this.checkedSupplier = checkedSupplier;
        }

        @Override
        public void run() {
            Instant startTime = Instant.now();
            logger.info("Refreshing at {}", startTime);

            Collection<T> collection;
            try {
                // This HAS to be called here, else the refresh logic is called to early
                // (clear cache -> call update logic, this takes time and the cache is empty -> fill cache)
                // which could lead to an empty cache.
                // DO NOT INLINE
                collection = checkedSupplier.get();
            } catch (Exception exc) {
                logger.info("Can't refresh cache", exc);
                return;
            }
            cached.clear();
            cached.addAll(collection);

            lastSuccessfulUpdate = startTime;
            long duration = Duration.between(startTime, Instant.now()).toMillis();
            logger.info("Refreshing took {} ms", duration);
        }
    }
}
