package com.izzy.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        String msg = "{\"firstName\":\"Charger_32\",\"phoneNumber\":\"32001122\",\"userManager\":26,\"role\":[\"charger\"],\"id\":1291}";
        historyRepository.save(new History("user", "create", userId, msg));
        historyRepository.save(new History("user", "update", userId, msg));
        historyRepository.save(new History("user", "delete", userId, msg));
        historyRepository.save(new History("task", "create", userId, msg));
        historyRepository.save(new History("task", "update", userId, msg));
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

    @Test
    public void findByFilteringForUserId() throws JsonProcessingException {
        Page<History> historyPage = historyRepository.findByFiltering(null, null, null, null, userId, PageRequest.of(0, 10));
        assertNotNull(historyPage, "Should find any history");
        assertFalse(historyPage.getContent().isEmpty(), "Should find any history");
        assertEquals(5, historyPage.getTotalElements(),"Should find 5 history records");
        History history = historyPage.getContent().get(0);
        String str = (new ObjectMapper()).writeValueAsString(history);
        System.out.println(str);
    }
}