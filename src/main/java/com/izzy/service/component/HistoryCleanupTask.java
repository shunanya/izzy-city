package com.izzy.service.component;

import com.izzy.repository.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class HistoryCleanupTask {
    private static final Logger logger = LoggerFactory.getLogger(HistoryCleanupTask.class);

    private final HistoryRepository historyRepository;

    public HistoryCleanupTask(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

//    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the 1st day of each month.
    @Transactional
    public void deleteOldHistory() {
        Timestamp cutoffDate = Timestamp.from(Instant.now().minus(1, ChronoUnit.YEARS));
        try {
            historyRepository.deleteOlderThan(cutoffDate);
        } catch (Exception e) {
            logger.error("Failed to delete old history records: {}", e.getMessage());
        }
    }


}
