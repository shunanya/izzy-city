package com.izzy.tests;

import com.izzy.security.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@SpringBootTest
public class UtilsTest {

    @ParameterizedTest
    @ValueSource(strings={"25..50","..40","20..","20<","<50","10-40","60"})
    public void test_data_range(String input) {
            List<Integer> result = Utils.parseDataRangeToPairOfInteger(input, 0, 100);

            assertNotNull(result, "result shouldn't be null");
            assertEquals(2, result.size(), "result.size() should be 2");
            // Print the result
            System.out.printf("String '%s' is separated to result '%s'%n", input, result);
    }

    @ParameterizedTest
    @ValueSource(strings={"01/01/2023..31/12/2023","..2024","2023..","03/09/2024","2024", ""})
    public void test_parse_valid_date_range(String input) {
        List<Instant> result = Utils.parseDateRangeToPairOfInstants(input);

        assertNotNull(result, "result shouldn't be null");
        assertEquals(2, result.size(), "result.size() should be 2");
        // Print the result
        System.out.printf("String '%s' is separated to result '%s'%n", input, result);
    }

    @Test
    public void test_handle_empty_input() {
        List<Instant> resultEmpty = Utils.parseDateRangeToPairOfInstants("");
        assertFalse(resultEmpty.isEmpty(), "result shouldn't be empty");
        assertEquals(2, resultEmpty.size(), "resultEmpty.size() should be 2");
    }

    @Test
    public void test_handle_null_input() {
        List<Instant> resultNull = Utils.parseDateRangeToPairOfInstants(null);
        assertFalse(resultNull.isEmpty(), "shouldn't be empty");
        assertEquals(2, resultNull.size(), "resultEmpty.size() should be 2");
    }

    @ParameterizedTest
    @ValueSource(strings={"01/01/2023..31/12/2023","..2024","2023..","03/09/2024","2023", ""})
    public void test_parse_valid_date_range_to_long(String input) {
        List<Long> result = Utils.parseDateRangeToPairOfLong(input);

        assertNotNull(result, "result shouldn't be null");
        assertEquals(2, result.size(), "result.size() should be 2");
        // Print the result
        System.out.printf("String '%s' is separated to result '%s'%n", input, result);
    }

    @ParameterizedTest
    @ValueSource(strings={"01/01/2023..31/12/2023","..2024","2023..","03/09/2024","2024", ""})
    public void test_parse_valid_date_range_to_timestamp(String input) {
        List<Timestamp> result = Utils.parseDateRangeToPairOfTimestamps(input);

        assertNotNull(result, "result shouldn't be null");
        assertEquals(2, result.size(), "result.size() should be 2");
        // Print the result
        System.out.printf("String '%s' is separated to result '%s'%n", input, result);
    }

}

