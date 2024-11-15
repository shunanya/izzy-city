package com.izzy.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izzy.model.History;
import com.izzy.payload.response.HistoryDTO;
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
import java.util.Map;

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
     * @param type optional filtering parameter (allowed values ('user', 'order', 'task', 'notification'))
     * @param action optional filtering parameter (allowed values ('create', 'update', 'delete', 'complete', 'cancel', approve', 'reject'))
     * @param userId optional filtering parameter - ID of the user who performed the mentioned action.
     * @param createdAt optional filtering parameter (concrete date or range of dates)
     * @return the filtered and sorted page of history records (normally {@link HistoryDTO})
     */
    public Page<?> getHistory(int page, // defaultValue = "0"
                                    int size, // defaultValue = "10"
                                    String sortBy, // defaultValue = "createdAt"
                                    @Nullable String type,
                                    @Nullable String action,
                                    @Nullable Long userId,
                                    @Nullable String createdAt // concrete date or range of dates
    ) {
        List<Timestamp> cat = Utils.parseDateRangeToPairOfTimestamps(createdAt);
        return historyRepository.findByFiltering(cat.get(0), cat.get(1), type, action, userId, PageRequest.of(page, size, Sort.by(sortBy).descending()))
                .map(history -> {
                    HistoryDTO dto = new HistoryDTO();
                    dto.setCreatedAt(history.getCreatedAt());
                    dto.setType(history.getType());
                    dto.setAction(history.getAction());
                    dto.setUserId(history.getUserId());
                    String description = history.getDescription();

                    try {// Try to parse the description as JSON
                        Map<String, Object> parsedMap = (new ObjectMapper()).readValue(description, new TypeReference<>() {});
                        dto.setDescription(parsedMap);
                    } catch (JsonMappingException e) {// Fallback to storing description as a raw string if not valid JSON
                        dto.setDescription(description);
                    } catch (Exception e) {
                        dto.setDescription(null); // or handle the exception as needed
                    }
                    return dto;
                });
    }

    @Transactional
    public void insertHistory(@NonNull String type, @NonNull String action, @NonNull String msg) {
        if (History.Type.getTypeByValue(type) == History.Type.UNDEFINED || History.Action.getActionByValue(action) == History.Action.UNDEFINED) {
            logger.error(String.format("Parameter type '%s' or action '%s' is undefined.%n", type, action));
        }
        History history = new History(type, action, customService.currentUserId(), msg.replaceAll("\\s", ""));
        historyRepository.save(history);
    }
}
