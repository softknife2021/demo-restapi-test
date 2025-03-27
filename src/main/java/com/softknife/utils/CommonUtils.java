package com.softknife.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * @author amatsaylo on 3/13/25
 * @project demo-restapi-test
 */

public class CommonUtils {

    // Private static instance of the class
    private static CommonUtils instance;

    // Private constructor to prevent instantiation from outside
    private CommonUtils() {
        // Constructor logic (if needed)
    }

    // Public method to provide access to the singleton instance
    public static CommonUtils getInstance() {
        if (instance == null) {
            synchronized (CommonUtils.class) {
                if (instance == null) {
                    instance = new CommonUtils();
                }
            }
        }
        return instance;
    }

    // Method to generate a unique ID based on timestamp
    public String generateUniqueId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return dateFormat.format(new Date());
    }

    // Example of another common function
    public String formatDate(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public String readResourceFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + fileName);
            }
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    public String generateRunId() {
        String timestamp = Instant.now().toString(); // ISO 8601 format
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 6);
        return "run_" + timestamp + "_" + uniqueSuffix;
    }

    /**
     * Gets duration between two dates in specific time unit
     * @param start Start date
     * @param end End date
     * @param unit Time unit (ChronoUnit)
     * @return Duration in specified unit
     */
    public long getDurationInUnit(Date start, Date end, ChronoUnit unit) {
        return unit.between(
                start.toInstant(),
                end.toInstant()
        );
    }

}
