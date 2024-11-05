package com.izzy.repository;

import com.izzy.model.History;
import com.izzy.security.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HistoryRepositoryTest {

    private final Long userId = 1L;
    @Autowired
    private HistoryRepository historyRepository;

    @BeforeEach
    void setUp() {
        historyRepository.save(new History("user", "create", userId, "create kuku blin"));
        historyRepository.save(new History("user", "update", userId, "update kuku blin"));
        historyRepository.save(new History("user", "delete", userId, "delete kuku blin"));
        historyRepository.save(new History("task", "create", userId, "create task blin"));
        historyRepository.save(new History("task", "update", userId, "update task blin"));
//        System.out.println(historyRepository.findAll(PageRequest.of(0, 10, Sort.by("createdAt").descending())).getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user","task"})
    public void findByFilteringType(String input) {
        Page<History> historyList = historyRepository.findByFiltering(null, null,
                input,
                null, userId,
                PageRequest.of(0, 10, Sort.by("createdAt").descending()));
        assertNotNull(historyList, "Should find any history");
        assertFalse(historyList.getContent().isEmpty(), "Should find any history");
        switch (input){
            case "user" -> assertEquals(3, historyList.getTotalElements(),"Should find 3 history records");
            case "task" -> assertEquals(2, historyList.getTotalElements(),"Should find 2 history records");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023..","-2025",""})
    public void findByFilteringDate(String input) {
        List<Timestamp> range = Utils.parseDateRangeToPairOfTimestamps(input);
        Page<History> historyPage = historyRepository.findByFiltering(range.get(0), range.get(1), null, null, userId, PageRequest.of(0, 10));
        assertNotNull(historyPage, "Should find any history");
        assertFalse(historyPage.getContent().isEmpty(), "Should find any history");
        assertEquals(5, historyPage.getTotalElements(),"Should find 5 history records");
    }

    @Test
    public void findByFilteringNoData() {
        List<Timestamp> range = Utils.parseDateRangeToPairOfTimestamps("2023");
        Page<History> historyPage = historyRepository.findByFiltering(range.get(0), range.get(1), null, null, userId, PageRequest.of(0, 10));
        assertNotNull(historyPage, "Should find any history");
        assertTrue(historyPage.getContent().isEmpty(), "Should find no any history");
    }
}