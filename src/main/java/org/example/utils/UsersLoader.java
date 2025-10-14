package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.config.Config;
import org.example.config.EnvironmentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class UsersLoader {
    private static final Logger logger = LoggerFactory.getLogger(UsersLoader.class);

    private static final String DEFAULT_USERS_FILE = "src/test/resources/creds/users-qa.json";
    private static final ConcurrentHashMap<String, Map<String, UserCredentials>> cache = new ConcurrentHashMap<>();

    private UsersLoader() {}

    public static String resolveUsersFilePath() {
        String explicit = System.getProperty("users.file");
        if (explicit != null && !explicit.trim().isEmpty()) {
            logger.info("Using explicit users.file: {}", explicit);
            return explicit;
        }

        String envProp = Config.getOrDefault("env", "qa"); // default to qa if absent
        EnvironmentManager env = EnvironmentManager.fromString(envProp);
        String envName = env.name().toLowerCase(); // "qa", "stage", "prod"
        String envFile = "src/test/resources/creds/users-" + envName + ".json";
        File fEnv = new File(envFile);
        if (fEnv.exists()) {
            logger.info("Using environment-specific users file: {}", envFile);
            return envFile;
        }

        File fDefault = new File(DEFAULT_USERS_FILE);
        if (fDefault.exists()) {
            logger.info("Environment-specific users file not found. Falling back to default: {}", DEFAULT_USERS_FILE);
            return DEFAULT_USERS_FILE;
        }

        logger.warn("Neither env-specific nor default users file exists. Will attempt to read {}", envFile);
        return envFile;
    }

    public static Map<String, UserCredentials> getUsersMap() {
        String path = resolveUsersFilePath();
        return cache.computeIfAbsent(path, p -> {
            List<UserCredentials> list = loadUsersList(p);
            ConcurrentHashMap<String, UserCredentials> map = new ConcurrentHashMap<>();
            if (list != null) {
                for (UserCredentials u : list) {
                    if (u != null && u.getId() != null) {
                        map.put(u.getId(), u);
                        map.put(u.getId().toLowerCase(Locale.ROOT), u);
                    } else {
                        logger.warn("Skipping invalid user entry in {}: {}", p, u);
                    }
                }
            }
            logger.info("Loaded {} user entries from {}", map.size()/2, p); // map stores two keys per user due to lowercase mapping
            return map;
        });
    }

    private static List<UserCredentials> loadUsersList(String path) {
        try (FileReader fr = new FileReader(new File(path))) {
            Gson gson = new Gson();
            Type t = new TypeToken<Map<String, List<UserCredentials>>>() {}.getType();
            Map<String, List<UserCredentials>> map = gson.fromJson(fr, t);
            List<UserCredentials> users = map != null ? map.get("users") : null;
            return users != null ? users : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Failed to load users JSON from {} - {}", path, e.getMessage());
            throw new RuntimeException("Failed to load users JSON: " + path, e);
        }
    }

    public static UserCredentials getUserById(String id) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        Map<String, UserCredentials> map = getUsersMap();
        UserCredentials found = map.get(id);
        if (found == null) found = map.get(id.toLowerCase(Locale.ROOT));
        return found;
    }

    public static List<UserCredentials> getAllUsers() {
        Map<String, UserCredentials> map = getUsersMap();
        // we stored both original and lowercase keys; filter unique objects via a Set
        Set<UserCredentials> set = new LinkedHashSet<>(map.values());
        return new ArrayList<>(set);
    }
}
