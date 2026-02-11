package com.eaapp.tests.utilities;

import org.testng.Assert;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class GetLocatorsFromLLM {

    public LocatorSuggestions getHealedLocators(LLMClient llmClient, String locType, String locValue,String pagesource) {
        String prompt = "The Selenium web element with locatorType: " + locType + " and locator value: " + locValue
                + " cannot be found on the page.\n"
                + "Based on the current page source, suggest alternative Selenium locators that might work.\n"
                + "\n"
                + "IMPORTANT: Return ONLY a valid JSON object with these keys: id, name, xpath, cssSelector, className, linkText.\n"
                + "- id\n"
                + "- name\n"
                + "- xpath\n"
                + "- cssSelector\n"
                + "- className\n"
                + "- linkText\n"
                + "\n"
                + "Format as a proper JSON with double quotes. Do not include any text before or after the JSON object.\n"
                + "Do not include explanations or comments, just return the JSON object.\n"
                + "\n"
                + "Page source (truncated): " + pagesource;

        String response = null;
        try {
            response = llmClient.callLLMForLocator(locType, locValue, prompt);
        } catch (Exception e) {

            return null;
        }
        if (response != null && !response.isBlank()) {
            try {

                ObjectMapper mapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build();
                LocatorSuggestions suggestions = mapper.readValue(response.trim(), LocatorSuggestions.class);

                return suggestions;
            } catch (Exception e) {
                Assert.fail("Error deserializing LLM locator suggestions: " + e.getMessage() + " | Raw result: " + response,e);
            }
        } 
        return null;
    }
}
