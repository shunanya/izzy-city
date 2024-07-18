package com.izzy.exception.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
}
