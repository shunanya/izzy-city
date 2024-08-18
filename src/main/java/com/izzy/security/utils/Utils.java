package com.izzy.security.utils;

import com.izzy.model.OrderScooter;
import com.izzy.model.misk.Task;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static final Pattern[] pattern = {Pattern.compile("ERROR.*(?=\\n)"), Pattern.compile("^.*(?=\\()")};

    public static String substringErrorFromException(Exception e) {
        String message = e.getMessage();
        for (Pattern p : pattern) {
            Matcher m = p.matcher(message);
            if (m.find()) {
                message = m.group();
                break;
            }
        }
        return message;
    }

    public static List<Long> parseDateRangeToPairOfLong(String range) {
        return parseDateRangeToPairOfTimestamps(range).stream().map(Timestamp::getTime).collect(Collectors.toList());
    }

    public static List<Timestamp> parseDateRangeToPairOfTimestamps(String range) {
        return parseDateRangeToPairOfInstants(range).stream().map(Timestamp::from).collect(Collectors.toList());
    }

    public static List<Instant> parseDateRangeToPairOfInstants(String range) {
        List<Instant> min_max = new ArrayList<>(2);
        // Split the range by "-"
        String[] parts = range.split("-");

        // Handle different cases
        if (parts.length == 2) { // Normal range
            min_max.add(0, convertToInstant(parts[0].isBlank() ? "2000" : parts[0]));
            min_max.add(1, convertToInstant(parts[1]));
            return min_max;
        } else if (parts.length == 1 && !range.contains("-")) { // Single year
            min_max.add(0, convertToInstant(parts[0]));
            min_max.add(1, convertToInstant(parts[0]));
            return min_max;
        } else if (range.startsWith("-")) { // Before a certain year
            min_max.add(0, convertToInstant("2000"));
            min_max.add(1, convertToInstant(range.substring(1)));
            return min_max;
        } else if (range.endsWith("-")) { // After a certain year
            min_max.add(0, convertToInstant(parts[0]));
            min_max.add(1, Instant.now());
            return min_max;
        } else { // Implied present
            return min_max;
        }
    }

    public static Instant convertToInstant(String dateString) {
        String[] patterns = new String[]{"yyyy", "MM/yyyy", "dd/MM/yyyy"};

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                switch (pattern) {
                    case "yyyy" -> {
                        Year year = Year.parse(dateString, formatter);
                        return convertToInstant(year);
                    }
                    case "MM/yyyy" -> {
                        YearMonth yearMonth = YearMonth.parse(dateString, formatter);
                        return convertToInstant(yearMonth);
                    }
                    case "dd/MM/yyyy" -> {
                        LocalDate date = LocalDate.parse(dateString, formatter);
                        return convertToInstant(date);
                    }
                    default ->
                            throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", dateString));
                }
            } catch (DateTimeParseException e) {
                // Continue trying with the next pattern
            }
        }
        return null;
    }

    private static Instant convertToInstant(Object date) {
        if (date instanceof Year year) {
            // Convert Year to Instant class using the first day of the year
            LocalDateTime dateTime = year.atDay(1).atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } else if (date instanceof YearMonth yearMonth) {
            // Convert YearMonth to Instant class using the first day of the month
            LocalDateTime dateTime = yearMonth.atDay(1).atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } else if (date instanceof LocalDate localDate) {
            // Convert LocalDate to Instant class using the start of the day
            LocalDateTime dateTime = localDate.atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        }
        return null;
    }

    public static List<Task> convertOrderScooterToTasks(List<OrderScooter> orderScooters) {
        List<Task> tasks = new ArrayList<>();
        if (orderScooters != null && !orderScooters.isEmpty()) {
            tasks = orderScooters.stream().map(os -> new Task(os.getOrder().getId(), os.getScooter().getId(), os.getPriority(), os.getComment())).collect(Collectors.toList());
        }
        return tasks;
    }

    public static List<Task> rearrangeTasksPriorities(@NonNull List<Task> tasks) {
        if (tasks.size() > 0) {
            tasks.sort(Comparator.comparingInt(Task::getPriority));
            int i = 1;
            for (Task task : tasks) {
                if (task.getPriority() > 0) task.setPriority(i++);
            }
        }
        return tasks;
    }

    public static List<OrderScooter> rearrangeOrderScooterPriorities(@NonNull List<OrderScooter> orderScooters) {
        if (orderScooters.size() > 0) {
            orderScooters.sort(Comparator.comparingInt(OrderScooter::getPriority));
            int i = 1;
            for (OrderScooter os : orderScooters) {
                if (os.getPriority() > 0) os.setPriority(i++);
            }
        }
        return orderScooters;
    }

}
