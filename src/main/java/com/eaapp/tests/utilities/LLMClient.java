package com.eaapp.tests.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class LLMClient {
    private static final Logger logger = Logger.getLogger(LLMClient.class);
    private static final String DEFAULT_ENDPOINT = "http://localhost:11434/api/generate";
    private static final String CONFIG_FILE = "llm-config.json";

    private final HttpClient httpClient;
    private final String endpoint;
    private final String model;
    private final boolean stream;
    private final Gson gson;
    private JsonObject configData;

    public LLMClient() {
        this(DEFAULT_ENDPOINT);
    }

    public LLMClient(String endpoint) {
        this.httpClient = HttpClient.newHttpClient();
        this.endpoint = endpoint != null ? endpoint : DEFAULT_ENDPOINT;
        this.gson = new Gson();
        this.configData = loadConfigFromJson();
        
        this.model = configData.has("model") ? configData.get("model").getAsString() : "llama3.2:latest";
        this.stream = configData.has("stream") ? configData.get("stream").getAsBoolean() : false;
        
        logger.info("LLMClient initialized - Endpoint: " + this.endpoint + ", Model: " + this.model + ", Stream: " + this.stream);
    }

    private JsonObject loadConfigFromJson() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream == null) {
                logger.warn("Configuration file not found: " + CONFIG_FILE + ". Using default values.");
                return new JsonObject();
            }

            String jsonContent = convertInputStreamToString(inputStream);
            logger.debug("Loaded config from " + CONFIG_FILE + ": " + jsonContent);
            return JsonParser.parseString(jsonContent).getAsJsonObject();

        } catch (IOException e) {
            logger.error("Error reading configuration file: " + e.getMessage(), e);
            return new JsonObject();
        }
    }

    private String convertInputStreamToString(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public String callLLMForLocator(String locType,String locValue, String prompt) throws Exception {
        logger.info("Calling LLM with prompt: " + prompt);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("stream", stream);

        String json = gson.toJson(requestBody);
        logger.debug("Request Body: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

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
            String result = jsonResponse.get("response").getAsString();

            logger.info("LLM Response: " + result);
            return result;

        } catch (Exception e) {
            logger.error("Error calling LLM: " + e.getMessage(), e);
            throw e;
        }
    }

     public String callLLM(String prompt) throws Exception {
        logger.info("Calling LLM with prompt: " + prompt);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("prompt", prompt);
        requestBody.addProperty("stream", stream);

        String json = gson.toJson(requestBody);
        logger.debug("Request Body: " + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

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
            String result = jsonResponse.get("response").getAsString();

            logger.info("LLM Response: " + result);
            return result;

        } catch (Exception e) {
            logger.error("Error calling LLM: " + e.getMessage(), e);
            throw e;
        }
    }
}
