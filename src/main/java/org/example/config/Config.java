package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public final class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final Properties props = new Properties();
    private static final String DEFAULT_CONFIG = "src/test/resources/config.properties";

    static {
        String configPath = System.getProperty("config", DEFAULT_CONFIG);
        FileInputStream fis = null;
        try {
            File f = new File(configPath);
            if (!f.exists()) {
                logger.warn("Config file not found at {}. Falling back to classpath/default file.", configPath);
                f = new File(DEFAULT_CONFIG);
            }
            fis = new FileInputStream(f);
            props.load(fis);
            logger.info("Loaded config from: {}", f.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to load config: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to load config file: " + configPath, e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ignore) {}
            }
        }
    }

    private Config() {}

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for key {}: {}. Returning default {}", key, val, defaultValue);
            return defaultValue;
        }
    }
}
