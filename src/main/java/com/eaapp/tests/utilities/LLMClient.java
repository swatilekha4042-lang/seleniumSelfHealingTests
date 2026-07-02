package com.eaapp.tests.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LLMClient {
    private static final Logger logger = Logger.getLogger(LLMClient.class);
    private static final String DEFAULT_ENDPOINT = "https://ollama.com/api/chat";
    private static final String CONFIG_FILE = "llm-config.json";
    private static final String API_KEY_ENV_VAR = "OLLAMA_API_KEY";

    private final HttpClient httpClient;
    private final String endpoint;
    private final String model;
    private final boolean stream;
    private final String apiKey;
    private final Gson gson;
    private JsonNode configData;

    public LLMClient() {
        this(null);
    }

    public LLMClient(String endpoint) {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.configData = loadConfigFromJson();
        this.endpoint = endpoint != null ? endpoint : getConfigValue("endpoint", DEFAULT_ENDPOINT);

        this.model = getConfigValue("model", "gpt-oss:120b");
        this.stream = configData.has("stream") && !configData.get("stream").isNull() ? configData.get("stream").asBoolean() : false;
        this.apiKey = System.getenv(API_KEY_ENV_VAR);

        logger.info("LLMClient initialized - Endpoint: " + this.endpoint + ", Model: " + this.model + ", Stream: " + this.stream);
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Environment variable " + API_KEY_ENV_VAR + " is not set. Hosted LLM calls may return 401 Unauthorized.");
        }
    }

    private String getConfigValue(String key, String defaultValue) {
        return configData.has(key) && !configData.get(key).isNull() ? configData.get(key).asText() : defaultValue;
    }

    private JsonNode loadConfigFromJson() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                logger.warn("Configuration file not found: " + CONFIG_FILE + ". Using default values.");
                return new ObjectMapper().createObjectNode();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonConfig = mapper.readTree(inputStream);
            logger.debug("Loaded config from " + CONFIG_FILE + ": " + jsonConfig.toString());
            return jsonConfig;

        } catch (IOException e) {
            logger.error("Error reading configuration file: " + e.getMessage(), e);
            return new ObjectMapper().createObjectNode();
        }
    }

    public String callLLMForLocator(String locType,String locValue, String prompt) throws Exception {
        logger.info("Calling LLM with prompt: " + prompt);

        JsonObject requestBody = createChatRequestBody(prompt);
        String json = gson.toJson(requestBody);
        logger.debug("Request Body: " + json);

        HttpRequest request = createChatRequest(json);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorMsg = "LLM API returned status " + response.statusCode() + ": " + response.body();
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String responseBody = response.body();
            logger.debug("Response Body: " + responseBody);

            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            String result = extractLLMContent(jsonResponse);

            logger.info("LLM Response: " + result);
            return result;

        } catch (Exception e) {
            logger.error("Error calling LLM: " + e.getMessage(), e);
            throw e;
        }
    }

    public String callLLM(String prompt) throws Exception {
        logger.info("Calling LLM with prompt: " + prompt);

        JsonObject requestBody = createChatRequestBody(prompt);
        String json = gson.toJson(requestBody);
        logger.debug("Request Body: " + json);

        HttpRequest request = createChatRequest(json);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorMsg = "LLM API returned status " + response.statusCode() + ": " + response.body();
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            String responseBody = response.body();
            logger.debug("Response Body: " + responseBody);

            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            String result = extractLLMContent(jsonResponse);

            logger.info("LLM Response: " + result);
            return result;

        } catch (Exception e) {
            logger.error("Error calling LLM: " + e.getMessage(), e);
            throw e;
        }
    }

    private JsonObject createChatRequestBody(String prompt) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(userMessage);

        requestBody.add("messages", messages);
        requestBody.addProperty("stream", stream);
        return requestBody;
    }

    private HttpRequest createChatRequest(String json) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));

        if (apiKey != null && !apiKey.isBlank()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        return builder.build();
    }

    private String extractLLMContent(JsonObject jsonResponse) {
        if (jsonResponse.has("message") && jsonResponse.get("message").isJsonObject()) {
            JsonObject message = jsonResponse.getAsJsonObject("message");
            if (message.has("content") && !message.get("content").isJsonNull()) {
                return message.get("content").getAsString();
            }
        }

        if (jsonResponse.has("response") && !jsonResponse.get("response").isJsonNull()) {
            return jsonResponse.get("response").getAsString();
        }

        throw new IllegalStateException("LLM response did not include message.content: " + jsonResponse);
    }
}
