package com.eaapp.tests.utilities;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SelfHealingLocators {
    private WebDriver driver;
    private By currentStrategy;
    private Map<String, By> locatorMap = new HashMap<>();
    
    // Persistence cache fields
    private static final String CACHE_FILE_PATH = "target/locator_cache.json";
    private static Map<String, Map<String, String>> persistentCache = new HashMap<>();
    private static boolean cacheLoaded = false;

    public SelfHealingLocators(WebDriver driver, By primaryLocator) {
        this.driver = driver;
        this.currentStrategy = primaryLocator;
        this.locatorMap.put("primary", primaryLocator);
        // Load cache on first instantiation
        if (!cacheLoaded) {
            loadCacheFromFile();
            cacheLoaded = true;
        }
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

        // Step:3 - Check if corrected locator exists in persistent cache
        By cachedLocator = getCachedLocator(currentStrategy);
        if (cachedLocator != null) {
            try {
                element = driver.findElement(cachedLocator);
                if (element != null) {
                    currentStrategy = cachedLocator;
                    locatorMap.put("cached", cachedLocator);
                    return element;
                }
            } catch (NoSuchElementException e) {
                // Cached locator failed, remove it and proceed to AI healing
                removeCachedLocator(currentStrategy);
            }
        }

        // Step:4 - AI Healing locator strategy with cache persistence
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
                
                // Cache the successful healed locator
                if (addedCount > 0) {
                    saveCorrectedLocatorToCache(currentStrategy, locatorMap.get("xpath") != null ? locatorMap.get("xpath") : 
                                                 locatorMap.get("id") != null ? locatorMap.get("id") : 
                                                 locatorMap.get("cssSelector"));
                }
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

    // ==================== PERSISTENCE CACHE METHODS ====================
    
    /**
     * Retrieves a cached corrected locator for a given original locator.
     */
    private By getCachedLocator(By originalLocator) {
        String locatorKey = originalLocator.toString();
        if (persistentCache.containsKey(locatorKey)) {
            Map<String, String> cachedEntry = persistentCache.get(locatorKey);
            String locType = cachedEntry.get("type");
            String locValue = cachedEntry.get("value");
            return createLocatorByType(locType, locValue);
        }
        return null;
    }

    /**
     * Removes a cached locator entry if it's no longer valid.
     */
    private void removeCachedLocator(By originalLocator) {
        String locatorKey = originalLocator.toString();
        persistentCache.remove(locatorKey);
        saveCacheToFile();
    }

    /**
     * Saves a corrected locator to the persistent cache.
     */
    private void saveCorrectedLocatorToCache(By originalLocator, By correctedLocator) {
        if (correctedLocator == null) return;
        
        String originalKey = originalLocator.toString();
        String correctedStr = correctedLocator.toString();
        
        int separatorIndex = correctedStr.indexOf(": ");
        String locType = correctedStr.substring(0, separatorIndex).trim();
        String locValue = correctedStr.substring(separatorIndex + 1).trim();
        
        Map<String, String> entry = new HashMap<>();
        entry.put("type", locType);
        entry.put("value", locValue);
        
        persistentCache.put(originalKey, entry);
        saveCacheToFile();
    }

    /**
     * Loads the locator cache from a JSON file.
     */
    private static void loadCacheFromFile() {
        File cacheFile = new File(CACHE_FILE_PATH);
        if (!cacheFile.exists()) {
            persistentCache = new HashMap<>();
            return;
        }
        
        try {
            String content = new String(java.nio.file.Files.readAllBytes(cacheFile.toPath()));
            Gson gson = new Gson();
            persistentCache = gson.fromJson(content, 
                new TypeToken<Map<String, Map<String, String>>>() {}.getType());
            if (persistentCache == null) {
                persistentCache = new HashMap<>();
            }
            System.out.println("Locator cache loaded successfully. Cache size: " + persistentCache.size());
        } catch (IOException e) {
            System.out.println("Failed to load locator cache: " + e.getMessage());
            persistentCache = new HashMap<>();
        }
    }

    /**
     * Saves the locator cache to a JSON file.
     */
    private static void saveCacheToFile() {
        try {
            File cacheFile = new File(CACHE_FILE_PATH);
            File parentDir = cacheFile.getParentFile();
            
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            Gson gson = new Gson();
            String json = gson.toJson(persistentCache);
            java.nio.file.Files.write(cacheFile.toPath(), json.getBytes());
            System.out.println("Locator cache saved successfully. Cache size: " + persistentCache.size());
        } catch (IOException e) {
            System.out.println("Failed to save locator cache: " + e.getMessage());
        }
    }

    /**
     * Helper method to create a By locator from type and value strings.
     */
    private By createLocatorByType(String locType, String locValue) {
        if (locValue == null || locValue.isBlank()) return null;
        
        String normalized = locType == null ? "" : locType.trim().toLowerCase();
        switch (normalized) {
            case "id":
                return By.id(locValue);
            case "name":
                return By.name(locValue);
            case "classname":
            case "class":
                return By.className(locValue);
            case "tagname":
            case "tag":
                return By.tagName(locValue);
            case "linktext":
                return By.linkText(locValue);
            case "partiallinktext":
                return By.partialLinkText(locValue);
            case "cssselector":
            case "css":
                return By.cssSelector(locValue);
            case "xpath":
                return By.xpath(locValue);
            default:
                return null;
        }
    }
}

