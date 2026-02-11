package com.eaapp.tests.core;

import com.eaapp.tests.config.TestConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

public class WebDriverFactory {
    private static final Logger logger = Logger.getLogger(WebDriverFactory.class);
    private final TestConfiguration config;

    public WebDriverFactory() {
        this.config = TestConfiguration.getInstance();
    }

    public WebDriver createDriver() {
        String browser = config.getBrowser().toLowerCase();
        WebDriver driver;

        switch (browser) {
            case "firefox":
                driver = createFirefoxDriver();
                break;
            case "edge":
                driver = createEdgeDriver();
                break;
            case "chrome":
            default:
                driver = createChromeDriver();
                break;
        }

        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWaitSeconds()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeoutSeconds()));

        // Maximize window if configured
        if (config.isMaximizeWindow()) {
            driver.manage().window().maximize();
        }

        logger.info("WebDriver initialized: " + browser);
        return driver;
    }

    private WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-extensions",
                "--disable-popup-blocking"
        );

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        return new FirefoxDriver(options);
    }

    private WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        return new EdgeDriver(options);
    }
}
