package com.eaapp.tests.core;

import com.eaapp.tests.config.TestConfiguration;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    private static final Logger logger = Logger.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected TestConfiguration config;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.config = TestConfiguration.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWaitSeconds()));
    }

    // Wait methods
    protected WebElement waitForElement(WebElement el) {
        return waitForElement(el, config.getExplicitWaitSeconds());
    }

    protected WebElement waitForElement(WebElement el, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(d -> {
            try {
                return (el != null && el.isDisplayed()) ? el : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    protected WebElement waitForElementToBeClickable(WebElement el) {
        return waitForElementToBeClickable(el, config.getExplicitWaitSeconds());
    }

    protected WebElement waitForElementToBeClickable(WebElement el, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(d -> {
            try {
                
                return (el != null && el.isDisplayed() && el.isEnabled()) ? el : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    protected boolean waitForElementToDisappear(By locator) {
        return waitForElementToDisappear(locator, config.getExplicitWaitSeconds());
    }

    protected boolean waitForElementToDisappear(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.debug("Element did not disappear within timeout: " + locator);
            return false;
        }
    }

    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
        logger.debug("Page loaded successfully");
    }

    protected boolean isElementDisplayed(WebElement el) {
        try {
            
            return el != null && el.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Action methods
    protected void click(WebElement ele) {
       
        ele.click();
        logger.debug("Clicked on element: " + ele);
    }

    protected void sendKeys(WebElement ele, String text) {
        
        ele.clear();
        ele.sendKeys(text);
        logger.debug("Entered text: " + text);
    }

    protected String getText(WebElement element) {
       
        return element.getText();
    }

    protected String getAttribute(WebElement element, String attribute) {
        
        return element.getAttribute(attribute);
    }

    protected void selectDropdownByText(WebElement element, String text) {
        
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.debug("Selected dropdown option: " + text);
    }

    protected void selectDropdownByValue(WebElement element, String value) {
       
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected dropdown value: " + value);
    }

    protected void scrollToElement(WebElement element) {
       
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: " + element);
    }

    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Sleep interrupted: " + e.getMessage());
        }
    }

    // Navigation
    protected void navigateTo(String url) {
        driver.navigate().to(url);
        waitForPageLoad();
        logger.info("Navigated to: " + url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    protected List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }
}
