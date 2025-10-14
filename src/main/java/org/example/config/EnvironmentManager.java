package org.example.config;

public enum EnvironmentManager {
    QA("https://www.saucedemo.com/v1/index.html"),
    STAGE("https://stage.example.com"),
    PROD("https://www.example.com");

    private final String baseUrl;

    EnvironmentManager(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static EnvironmentManager fromString(String env) {
        if (env == null) return QA;
        return switch (env.toLowerCase()) {
            case "production" -> PROD;
            case "staging" -> STAGE;
            default -> QA;
        };
    }
}
