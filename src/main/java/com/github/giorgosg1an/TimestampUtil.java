package com.github.giorgosg1an;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating formatted timestamp strings.
 * 
 * @author Giannopoulos Georgios
 */
public class TimestampUtil {
    /**
     * Returns the current date and time as a formatted string.
     * The format used is "yyyy-MM-dd HH:mm:ss.SSS", which includes the year, month, day,
     * hour, minute, second, and milliseconds.
     *
     * @return the current timestamp as a formatted string
     */
    public static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return LocalDateTime.now().format(formatter);
    }
}
