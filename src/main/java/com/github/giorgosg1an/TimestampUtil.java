package com.github.giorgosg1an;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampUtil {
    public static String now() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        return LocalDateTime.now().format(formatter);
    }
}
