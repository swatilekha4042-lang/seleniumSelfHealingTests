package com.eaapp.tests.core;

import com.eaapp.tests.config.TestConfiguration;
import com.eaapp.tests.utilities.ScreenshotHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.openqa.selenium.WebDriver;

public class TestBase {
    private static final Logger logger = Logger.getLogger(TestBase.class);
    protected WebDriver driver;
    protected TestConfiguration config;
    private WebDriverFactory driverFactory;

    public TestBase() {
        config = TestConfiguration.getInstance();
    }

    @BeforeMethod
    public void setUp() {
        driverFactory = new WebDriverFactory();
        driver = driverFactory.createDriver();
        driver.navigate().to(config.getBaseUrl());
        logger.info("Test setup completed");
    }

    @AfterMethod
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                logger.info("WebDriver closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error during tearDown: " + e.getMessage());
        }
    }

    protected void navigateToUrl(String relativeUrl) {
        if (driver != null) {
            driver.navigate().to(config.getBaseUrl() + relativeUrl);
            logger.info("Navigated to: " + relativeUrl);
        }
    }
}
