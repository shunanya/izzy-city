package com.izzy.service;

import com.izzy.model.History;
import com.izzy.repository.HistoryRepository;
import com.izzy.security.custom.service.CustomService;
import com.izzy.security.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class HistoryService {
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);

    private final HistoryRepository historyRepository;
    private final CustomService customService;

    public HistoryService(HistoryRepository historyRepository, CustomService customService) {
        this.historyRepository = historyRepository;
        this.customService = customService;
    }

    /**
     * Retrieve history by filtering
     * @param page the initial number for pagination (defaultValue = "0")
     * @param size the size of page for pagination (defaultValue = "10")
     * @param sortBy parameter to sort by (defaultValue = "createdAt")
     * @param type optional filtering parameter (allowed values ('user', 'order', 'task'))
     * @param action optional filtering parameter (allowed values ('create', 'update', 'delete'))
     * @param userId optional filtering parameter - ID of the user who performed the mentioned action.
     * @param createdAt optional filtering parameter (concrete date or range of dates)
     * @return the filtered and sorted page of history records
     */
    public Page<History> getHistory(int page, // defaultValue = "0"
                                    int size, // defaultValue = "10"
                                    String sortBy, // defaultValue = "createdAt"
                                    @Nullable String type,
                                    @Nullable String action,
                                    @Nullable Long userId,
                                    @Nullable String createdAt // concrete date or range of dates
    ) {
        List<Timestamp> cat = Utils.parseDateRangeToPairOfTimestamps(createdAt);
        return historyRepository.findByFiltering(cat.get(0), cat.get(1), type, action, userId, PageRequest.of(page, size, Sort.by(sortBy).descending()));
    }

    @Transactional
    public void insertHistory(@NonNull String type, @NonNull String action, @NonNull String description) {
        if (History.Type.getTypeByValue(type) == History.Type.UNDEFINED || History.Action.getActionByValue(action) == History.Action.UNDEFINED) {
            logger.error(String.format("Parameter type '%s' or action '%s' is undefined.%n", type, action));
        }
        History history = new History(type, action, customService.currentUserId(), description.replaceAll("\\s", ""));
        historyRepository.save(history);
    }
}
