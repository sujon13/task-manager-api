package com.example.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateUtil {
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";

    public static LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (Exception e) {
            log.error("Error parsing date: {}", date, e);
            return null;
        }
    }
}
