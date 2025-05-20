package org.ashwin.flash.database.scheduler;

import org.ashwin.flash.database.core.Compactor;

import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CompactionScheduler {
    private static final Logger LOGGER = Logger.getLogger(CompactionScheduler.class.getName());
    private final Compactor compactor;
    private final ScheduledExecutorService scheduler;
    private volatile boolean isCompactionRunning = false;

    public CompactionScheduler(Compactor compactor) {
        this.compactor = compactor;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startScheduledCompaction(long initialDelay, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(() -> {
            if (isCompactionRunning) {
                LOGGER.warning("Previous compaction still running, skipping this iteration");
                return;
            }

            try {
                isCompactionRunning = true;
                long startTime = System.currentTimeMillis();

                LOGGER.info("Starting scheduled compaction");
                compactor.compact();

                long duration = System.currentTimeMillis() - startTime;
                LOGGER.info("Compaction completed in " + duration + "ms");

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Compaction failed", e);
            } finally {
                isCompactionRunning = false;
            }
        }, initialDelay, period, timeUnit);
    }

    public void shutdown() {
        LOGGER.info("Shutting down compaction scheduler");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
