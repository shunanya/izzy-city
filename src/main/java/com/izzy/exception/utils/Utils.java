package com.izzy.exception.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final String patternStr = "ERROR.*(?=\\n)";
    public static String substringErrorFromException(Exception e){
        Pattern pattern = Pattern.compile(patternStr);
        String message = e.getMessage();
        Matcher matcher = pattern.matcher(message);

        // Check if there is a match
        if (matcher.find()) {
            return matcher.group();
        }
        // No matching substring found
        return message;
    }
}
