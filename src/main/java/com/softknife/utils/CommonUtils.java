package com.softknife.utils;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

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

}
