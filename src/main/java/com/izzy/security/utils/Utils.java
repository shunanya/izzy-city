package com.izzy.security.utils;

import com.izzy.model.Task;
import com.izzy.model.TaskDTO;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static final Pattern[] pattern = {Pattern.compile("ERROR.*(?=\\n)"), Pattern.compile("^.*(?=\\()")};
    private static final String phoneRegEx = "^\\+?\\d{0,3}?[-.\\s]?\\(?\\d{1,4}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$";

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

    /**
     * Parse string range of Data (Integer) to the list of 2 Integers
     * <p>This proxy method calls the original method with parameters min=0 and max=100.</p>
     *
     * @param range the string representing data range
     * @return the list of 2 numbers [min, max] in case the range is not null
     * @see #parseDataRangeToPairOfInteger(String, int, int) for more details
     */
    public static List<Integer> parseDataRangeToPairOfInteger(@Nullable String range) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        return parseDataRangeToPairOfInteger(range, 0, 100);
    }

    /**
     * Parse string range of Data (Integer) to the list of 2 Integers
     *
     * @param range the string representing data range
     *              <p>For example: '10..50'</p>
     *              <p>
     *              the range can have the following view (if min=0, max=100)
     *                           <ul>
     *                           <li>'2..3' normal range converts to [2,3]
     *                           <li>'3'    single number converts to [3,3]
     *                           <li>'..3'  before number converts to [0,3]
     *                           <li>'2..'  after number converts to [2,100]
     *                           </ul>
     *              </p>
     *              <p>
     *                Separator for range can be one of the following
     *                           <ui>
     *                           <li>'-'
     *                           <li>'<'
     *                           <li>'>'
     *                           <li>'..'
     *                           </ui>
     *              </p>
     * @param min   the default minimum number value
     * @param max   the default maximal number value
     * @return the list of 2 numbers [min, max] in case the range is not null
     */
    public static List<Integer> parseDataRangeToPairOfInteger(@Nullable String range, int min, int max) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        List<Integer> result = new ArrayList<>(2) {{
            add(min);
            add(max);
        }};
         // Regular expression for splitting by '-', '<', '>', or '..'
        String regex = "(-)|(<)|(>)|(\\.\\.)";
        String test = range.trim();
        // Split the range by separator and compose Integers list.
        List<Integer> parts = Arrays.stream(test.split(regex)).map(p -> p.isBlank() ? min : Integer.parseInt(p.replaceAll("\\D", ""))).sorted().toList();
        String sep = findSeparator(test, regex);
        // Handle different cases
        switch (parts.size()) {
            case 2 -> {// Normal range
                result.set(0, parts.get(0));
                result.set(1, parts.get(1));
            }
            case 1 -> {
                if (!range.contains(sep)) { // Single number
                    result.set(0, parts.get(0));
                    result.set(1, parts.get(0));
                } else if (range.startsWith(sep)) { // Before a certain number
                    result.set(1, parts.get(0));
                } else if (range.endsWith(sep)) { // After a certain number
                    result.set(0, parts.get(0));
                }
            }
        }
        return result;
    }

    public static String findSeparator(@NonNull String test, @NonNull String regex) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(test);

        while (matcher.find()) {
            result.add(matcher.group());
        }
        if (result.size() > 1)
            throw new IllegalArgumentException(String.format("Error: wrong construction of range '%s'.", test));
        return result.isEmpty() ? "!" : result.get(0);
    }

    public static List<Long> parseDateRangeToPairOfLong(@Nullable String range) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        return parseDateRangeToPairOfTimestamps(range).stream().map(Timestamp::getTime).collect(Collectors.toList());
    }

    public static List<Timestamp> parseDateRangeToPairOfTimestamps(@Nullable String range) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        return parseDateRangeToPairOfInstants(range).stream().map(Timestamp::from).collect(Collectors.toList());
    }

    public static List<LocalDate> parseDateRangeToPairOfLocalDate(@Nullable String range) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        return parseDateRangeToPairOfInstants(range).stream().map(i-> i.atZone(ZoneOffset.UTC).toLocalDate()).collect(Collectors.toList());
        // LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        // LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
        // LocalDate localDate = LocalDateTime.ofEpochSecond(unixTimestamp / 1000, 0, ZoneOffset.UTC).toLocalDate();
    }

    /**
     * parse range of Date representing by String
     *
     * @param range the string representing range of date
     * <p>
     * The separator can be one of '-', '<', '>', '..'
     * </p>
     * @return the list of 2 date
     */
    public static List<Instant> parseDateRangeToPairOfInstants(@Nullable String range) {
        if (range == null || range.isBlank()) {
            return new ArrayList<>(Collections.nCopies(2, null));
        }
        Instant min = convertToInstant("2000"); // minimal value is 01/01/2000
//        Instant max = LocalDate.now(ZoneOffset.UTC).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();// maximal value is end of the current day
        Instant max = convertToInstant_EndDate(Instant.now());  // maximal value is the start of next day
        List<Instant> result = new ArrayList<>(2){{
            add(min);
            add(max);
        }};

        String regex = "(-)|(<)|(>)|(\\.\\.)";
        String test = range.trim();

        // Split the range by separator
        List<Object> parts = Arrays.stream(test.split(regex)).map(p -> p.isBlank() ? convertToObject("2000") : convertToObject(p)).toList();
        String sep = findSeparator(test, regex);

        // Handle different cases
        switch (parts.size()) {
            case 2 -> {// Normal range
                result.set(0, convertToInstant_StartDate(parts.get(0)));
                result.set(1, convertToInstant_EndDate(parts.get(1)));
            }
            case 1 -> {
                if (!range.contains(sep)) { // Single number
                    result.set(0, convertToInstant_StartDate(parts.get(0)));
                    result.set(1, convertToInstant_EndDate(parts.get(0)));
                } else if (range.startsWith(sep)) { // Before a certain number
                    result.set(1, convertToInstant_EndDate(parts.get(0)));
                } else if (range.endsWith(sep)) { // After a certain number
                    result.set(0, convertToInstant_StartDate(parts.get(0)));
                }
            }
        }
        return result;
    }

    /**
     * Convert date representing by string to Object
     * @param dateString Date representing by String in one of form "yyyy", "MM/yyyy", "dd/MM/yyyy"
     * @return one of Object ({@link Year}, {@link YearMonth}, {@link LocalDate})
     */
    public static Object convertToObject(String dateString) {
        String[] patterns = new String[]{"yyyy", "MM/yyyy", "dd/MM/yyyy"};

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                switch (pattern) {
                    case "yyyy" -> {
                        return Year.parse(dateString, formatter);
                    }
                    case "MM/yyyy" -> {
                        return YearMonth.parse(dateString, formatter);
                    }
                    case "dd/MM/yyyy" -> {
                        return LocalDate.parse(dateString, formatter);
                    }
                    default ->
                            throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", dateString));
                }
            } catch (DateTimeParseException e) {
                // Continue trying with the next pattern
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", dateString));
    }

    public static Instant convertToInstant(String dateString) {
        String[] patterns = new String[]{"yyyy", "MM/yyyy", "dd/MM/yyyy"};

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                switch (pattern) {
                    case "yyyy" -> {
                        Year year = Year.parse(dateString, formatter);
                        return convertToInstant_StartDate(year);
                    }
                    case "MM/yyyy" -> {
                        YearMonth yearMonth = YearMonth.parse(dateString, formatter);
                        return convertToInstant_StartDate(yearMonth);
                    }
                    case "dd/MM/yyyy" -> {
                        LocalDate date = LocalDate.parse(dateString, formatter);
                        return convertToInstant_StartDate(date);
                    }
                    default ->
                            throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", dateString));
                }
            } catch (DateTimeParseException e) {
                // Continue trying with the next pattern
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", dateString));
    }

    /**
     * Convert Object representing date to Instance at start of date
     * <p>For example:
     * <ul>
     *     <li>2004 => 2004-01-01T00:00:00.0Z
     *     <li>01/2004 => 2004-01-01T00:00:00.0Z
     *     <li>05/01/2004 => 2004-01-05T00:00:00.0Z
     * </ul>
     * </p>
     *
     * @param date the object representing one of {@link Year}, {@link YearMonth}, {@link LocalDate}
     * @return {@link Instant} object
     */
    public static Instant convertToInstant_StartDate(Object date) {
        return convertToInstant(date, false);
    }

    /**
     * Convert Object representing date to Instance at end of date
     * <p>For example:
     * <ul>
     *     <li>2004 => 2005-01-01T00:00:00.0Z
     *     <li>01/2004 => 2004-02-01T00:00:00.0Z
     *     <li>05/01/2004 => 2004-01-06T00:00:00.0Z
     * </ul>
     * </p>
     * @param date the object representing one of {@link Year}, {@link YearMonth}, {@link LocalDate}
     * @return {@link Instant} object
     */
    public static Instant convertToInstant_EndDate(Object date) {
        return convertToInstant(date, true);
    }

    /**
     * Convert Object representing date to Instance
     * @param date the object representing one of {@link Year}, {@link YearMonth}, {@link LocalDate}
     * @param next boolean, convert to start of date on FALSE, convert to end of date on TRUE
     * @return {@link Instant} object
     */
    private static Instant convertToInstant(Object date, boolean next) {
        if (date instanceof Year year) {
            // Convert Year to Instant class using the first day of the year
            LocalDateTime dateTime = year.plus(next?1:0, ChronoUnit.YEARS).atDay(1).atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } else if (date instanceof YearMonth yearMonth) {
            // Convert YearMonth to Instant class using the first day of the month
            LocalDateTime dateTime = yearMonth.plus(next?1:0, ChronoUnit.MONTHS).atDay(1).atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } else if (date instanceof LocalDate localDate) {
            // Convert LocalDate to Instant class using the start of the day
            LocalDateTime dateTime = localDate.plusDays(next ? 1 : 0).atStartOfDay();
            return dateTime.toInstant(ZoneOffset.UTC);
        } else if (date instanceof Instant instant) {
            return instant.plus(next?1:0, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        }
        throw new IllegalArgumentException(String.format("Unsupported date type: '%s'", date.toString()));
    }

    public static List<Task> rearrangeTasksPriorities(@NonNull List<Task> tasks) {
        if (!tasks.isEmpty()) {
            tasks.sort(Comparator.comparingInt(Task::getPriority));
            int i = 1;
            for (Task task : tasks) {
                if (task.getPriority() > 0) task.setPriority(i++);
            }
        }
        return tasks;
    }

    public static List<TaskDTO> rearrangeTasksDTOPriorities(@NonNull List<TaskDTO> tasksDTO) {
        if (!tasksDTO.isEmpty()) {
            tasksDTO.sort(Comparator.comparingInt(TaskDTO::getPriority));
            int i = 1;
            for (TaskDTO taskDTO : tasksDTO) {
                if (taskDTO.getPriority() > 0) taskDTO.setPriority(i++);
            }
        }
        return tasksDTO;
    }

    public static boolean isCorrectPhoneNumber(String phoneNumber) {
        return Pattern.matches(phoneRegEx, phoneNumber);
    }
}
