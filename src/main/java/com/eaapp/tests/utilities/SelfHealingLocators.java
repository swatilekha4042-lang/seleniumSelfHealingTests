package com.eaapp.tests.utilities;

import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SelfHealingLocators {
    private WebDriver driver;
    private By currentStrategy;
    private Map<String, By> locatorMap = new HashMap<>();

    public SelfHealingLocators(WebDriver driver, By primaryLocator) {
        this.driver = driver;
        this.currentStrategy = primaryLocator;
        this.locatorMap.put("primary", primaryLocator);
    }

    private static final int MAX_RETRIES = 2;

    public WebElement findElementCustom() {
    return findElementCustom(MAX_RETRIES);
}

    public WebElement findElementCustom(int retryAttempt) {

        // Step:1 Find with the current starategy
        WebElement element = tryFindWithCurrentStrategy();
        if (element != null)
            return element;

        // Step:2 - If not found, try alternative strategies
        element = tryAlternativeStrategies();
        if (element != null)
            return element;

        // Step:3 - AI Healing locator strategy
        if (retryAttempt > 0) {
            healUsingAI();
            return findElementCustom(retryAttempt - 1);
        }
        throw new NoSuchElementException("Failed to locate element after retry attempts " + retryAttempt);

    }

    public WebElement tryFindWithCurrentStrategy() {
        try {
            return driver.findElement(currentStrategy);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public WebElement tryAlternativeStrategies() {
        if (locatorMap.size() <= 1)
            return null;
        for (Map.Entry<String, By> entry : locatorMap.entrySet()) {
            By strategy = entry.getValue();
            if (strategy.equals(currentStrategy))
                continue;
            try {
                WebElement element = driver.findElement(strategy);
                if (element != null) {
                    currentStrategy = strategy;
                    return element;
                }
            } catch (NoSuchElementException e) {
                // continue trying other strategies
            }
        }
        return null;
    }

    private void healUsingAI() {

        try {
            String strategyString = currentStrategy.toString();
            int separatorIndex = strategyString.indexOf(": ");

            String locType = strategyString.substring(0, separatorIndex);
            String locValue = strategyString.substring(separatorIndex + 1).trim();
            String pagesource = driver.getPageSource();

            LLMClient llmClient = new LLMClient();
            GetLocatorsFromLLM locSuggest = new GetLocatorsFromLLM();
            LocatorSuggestions locators = locSuggest.getHealedLocators(llmClient, locType, locValue, pagesource);

            if (locators != null) {
                int addedCount = 0;
                addedCount += tryCreateLocatorStrategy("id", locators.id);
                addedCount += tryCreateLocatorStrategy("name", locators.name);
                addedCount += tryCreateLocatorStrategy("xpath", locators.xpath);
                addedCount += tryCreateLocatorStrategy("cssSelector", locators.cssSelector);
                addedCount += tryCreateLocatorStrategy("className", locators.className);
                addedCount += tryCreateLocatorStrategy("linkText", locators.linkText);
            }

            // // Heuristic fallbacks for common id typos (e.g., pluralization)
            // if (locType != null && locType.toLowerCase().contains("id")) {
            //     addHeuristicIdVariants(locValue);
            // }
        } catch (Exception e) {
            System.out.println("AI Healing Failed");
        }

    }

    private int tryCreateLocatorStrategy(String locType, String locValue) {
        if (locValue == null || locValue.isBlank())
            return 0;
        try {
            By by = null;
            String normalized = locType == null ? "" : locType.trim().toLowerCase();
            switch (normalized) {
                case "id":
                    by = By.id(locValue);
                    break;
                case "name":
                    by = By.name(locValue);
                    break;
                case "classname":
                case "class":
                    by = By.className(locValue);
                    break;
                case "tagname":
                case "tag":
                    by = By.tagName(locValue);
                    break;
                case "linktext":
                    by = By.linkText(locValue);
                    break;
                case "partiallinktext":
                    by = By.partialLinkText(locValue);
                    break;
                case "cssselector":
                case "css":
                    by = By.cssSelector(locValue);
                    break;
                case "xpath":
                    by = By.xpath(locValue);
                    break;
                default:
                    return 0;
            }
            if (by != null) {
                locatorMap.put(normalized, by);
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    // private void addHeuristicIdVariants(String original) {
    //     if (original == null) return;
    //     String trimmed = original.trim();
    //     if (trimmed.isEmpty()) return;

    //     // If original ends with 's', try singular form
    //     if (trimmed.endsWith("s")) {
    //         tryCreateLocatorStrategy("id", trimmed.substring(0, trimmed.length() - 1));
    //     }
    //     // If original ends with 'Btn' but not 'Btns', try plural form
    //     if (trimmed.endsWith("Btn") && !trimmed.endsWith("Btns")) {
    //         tryCreateLocatorStrategy("id", trimmed + "s");
    //     }
    // }
}
