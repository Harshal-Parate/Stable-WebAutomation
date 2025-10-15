package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class DBUtils {

    private static final Logger log = LogManager.getLogger(DBUtils.class);
    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "password";


    private DBUtils() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            log.warn("DB Connection failed: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static ResultSet executeQuery(String query) {
        try {
            return getConnection().prepareStatement(query).executeQuery();
        } catch (SQLException e) {
            log.warn("Failed to get result set: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() throws SQLException {
        if (Objects.nonNull(getConnection()) && !getConnection().isClosed()) {
            getConnection().close();
        }
    }
}
