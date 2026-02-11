package com.eaapp.tests.core;

import com.eaapp.tests.config.TestConfiguration;
import com.eaapp.tests.utilities.SelfHealingLocators;
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
    protected WebElement waitForElement(By locator) {
        return waitForElement(locator, config.getExplicitWaitSeconds());
    }

    protected WebElement waitForElement(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(d -> {
            try {
                WebElement el = new SelfHealingLocators(d, locator).findElementCustom();
                return (el != null && el.isDisplayed()) ? el : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    protected WebElement waitForElementToBeClickable(By locator) {
        return waitForElementToBeClickable(locator, config.getExplicitWaitSeconds());
    }

    protected WebElement waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(d -> {
            try {
                WebElement el = new SelfHealingLocators(d, locator).findElementCustom();
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

    protected boolean isElementPresent(By locator) {
        try {
            new SelfHealingLocators(driver, locator).findElementCustom();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement el = new SelfHealingLocators(driver, locator).findElementCustom();
            return el != null && el.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Action methods
    protected void click(By locator) {
        WebElement element = waitForElementToBeClickable(locator);
        element.click();
        logger.debug("Clicked on element: " + locator);
    }

    protected void sendKeys(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
        logger.debug("Entered text in element: " + locator);
    }

    protected String getText(By locator) {
        WebElement element = waitForElement(locator);
        return element.getText();
    }

    protected String getAttribute(By locator, String attribute) {
        WebElement element = waitForElement(locator);
        return element.getAttribute(attribute);
    }

    protected void selectDropdownByText(By locator, String text) {
        WebElement element = waitForElement(locator);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.debug("Selected dropdown option: " + text);
    }

    protected void selectDropdownByValue(By locator, String value) {
        WebElement element = waitForElement(locator);
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected dropdown value: " + value);
    }

    protected void scrollToElement(By locator) {
        WebElement element = new SelfHealingLocators(driver, locator).findElementCustom();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        logger.debug("Scrolled to element: " + locator);
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
