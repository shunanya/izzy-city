package com.izzy.tests;

import com.izzy.model.Task;
import com.izzy.security.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestStatus {

    @Test
    public void checkTestStatus() {
        List<String> statusList = new ArrayList<>() {{
            add("CANCELED");
            add("completed");
            add("kuku");
        }};

        statusList.forEach(s -> System.out.println(Task.Status.getStatusByString(s)));
    }

    @Test
    public void checkDuration() {
        Long jwtRefreshExpirationMs = 86400000L;

        assertEquals(86400, Duration.ofMillis(jwtRefreshExpirationMs).toSeconds());
    }

    @Test
    public void checkListInitialization() {
        List<Long> list = new ArrayList<>(Collections.nCopies(2, null));
        System.out.printf("%s, %s", list.get(0), list.get(1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"30/07/2024", "07/2024", "2024"})
    public void checkInstant(String testDay) {
        Instant instant = Utils.convertToInstant(testDay);

        Instant startDay = instant;
        Instant endDay = instant.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        System.out.printf("%s => startDay: %s, endDay: %s", instant, startDay, endDay);
    }

    @Test
    public void checkInstantNow() {
        Instant now = Instant.now();

        Instant max = now.plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant min = now.truncatedTo(ChronoUnit.DAYS);
        System.out.printf("%s => min: %s, max: %s", now, min, max);

        assertEquals(min, max.minus(1, ChronoUnit.DAYS));
    }
}
