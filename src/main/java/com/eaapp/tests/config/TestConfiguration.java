package com.eaapp.tests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestConfiguration {
    private static final Logger logger = Logger.getLogger(TestConfiguration.class);
    private static TestConfiguration instance;
    private JsonNode configJson;

    private TestConfiguration() {
        loadConfiguration();
    }

    public static TestConfiguration getInstance() {
        if (instance == null) {
            instance = new TestConfiguration();
        }
        return instance;
    }

    private void loadConfiguration() {
        try {
            String configPath = System.getProperty("user.dir") + "/src/test/resources/application.json";
            String jsonContent = Files.readString(Paths.get(configPath));
            ObjectMapper mapper = new ObjectMapper();
            configJson = mapper.readTree(jsonContent);
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.warn("Could not load configuration file, using defaults: " + e.getMessage());
            configJson = new ObjectMapper().createObjectNode();
        }
    }

    // Test Settings
    public String getBaseUrl() {
        return getJsonString("testSettings.baseUrl", "https://www.linkedin.com/feed/");
    }

    public String getBrowser() {
        return getJsonString("testSettings.browser", "Chrome");
    }

    public int getImplicitWaitSeconds() {
        return getJsonInt("testSettings.implicitWaitSeconds", 10);
    }

    public int getExplicitWaitSeconds() {
        return getJsonInt("testSettings.explicitWaitSeconds", 30);
    }

    public int getPageLoadTimeoutSeconds() {
        return getJsonInt("testSettings.pageLoadTimeoutSeconds", 60);
    }

    public boolean isTakeScreenshotOnFailure() {
        return getJsonBoolean("testSettings.takeScreenshotOnFailure", true);
    }

    public String getScreenshotPath() {
        return getJsonString("testSettings.screenshotPath", "./screenshots");
    }

    public boolean isHeadless() {
        return getJsonBoolean("testSettings.headless", false);
    }

    public boolean isMaximizeWindow() {
        return getJsonBoolean("testSettings.maximizeWindow", true);
    }

    // Test Data
    public String getValidUsername() {
        return getJsonString("testData.validUsername", "testuser123");
    }

    public String getValidPassword() {
        return getJsonString("testData.validPassword", "Test@1234");
    }

    public String getValidEmail() {
        return getJsonString("testData.validEmail", "test@test.com");
    }

    public String getSearchEmployeeName() {
        return getJsonString("testData.searchEmployeeName", "Karthik");
    }

    // Timeouts
    public int getShortTimeout() {
        return getJsonInt("timeouts.short", 5);
    }

    public int getMediumTimeout() {
        return getJsonInt("timeouts.medium", 15);
    }

    public int getLongTimeout() {
        return getJsonInt("timeouts.long", 30);
    }

    // Helper methods
    private JsonNode getJsonElement(String path) {
        try {
            String[] keys = path.split("\\.");
            JsonNode current = configJson;
            for (String key : keys) {
                current = current.get(key);
                if (current == null) return null;
            }
            return current;
        } catch (Exception e) {
            logger.debug("Using default value for " + path);
            return null;
        }
    }

    private String getJsonString(String path, String defaultValue) {
        JsonNode element = getJsonElement(path);
        return element != null && element.isTextual() ? element.asText() : defaultValue;
    }

    private int getJsonInt(String path, int defaultValue) {
        JsonNode element = getJsonElement(path);
        return element != null && element.isInt() ? element.asInt() : defaultValue;
    }

    private boolean getJsonBoolean(String path, boolean defaultValue) {
        JsonNode element = getJsonElement(path);
        return element != null && element.isBoolean() ? element.asBoolean() : defaultValue;
    }
}
