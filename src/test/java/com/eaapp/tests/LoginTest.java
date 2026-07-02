package com.eaapp.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.eaapp.tests.config.TestConfiguration;
import com.eaapp.tests.core.WebDriverFactory;
import com.eaapp.tests.pages.LoginPage;
import com.eaapp.tests.utilities.ExtentManager;
import com.eaapp.tests.utilities.LLMClient;
import com.eaapp.tests.utilities.SelfHealingLocators;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest {
    private static final Logger logger = Logger.getLogger(LoginTest.class);
    private WebDriver driver;
    private LoginPage loginPage;
    private TestConfiguration config;
    private LLMClient llmClient;
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeClass
    public void setUp() {
        logger.info("========== Starting Test ==========");
        WebDriverFactory factory = new WebDriverFactory();
        driver = factory.createDriver();
        loginPage = new LoginPage(driver);
        config = TestConfiguration.getInstance();
        logger.info("WebDriver initialized successfully");
        llmClient = new LLMClient();
        extent = ExtentManager.getInstance();
        test = extent.createTest("Login Test");
    }

    @AfterClass
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                logger.info("WebDriver closed successfully");
                test.info("WebDriver closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error closing WebDriver: " + e.getMessage());
            test.info("Error closing WebDriver: " + e.getMessage());
        }
        extent.flush();
        logger.info("========== Test Complete ==========\n");
    }

    @Test(description = "Test enhanced login with LLM locator suggestion")
    public void testEnhancedLogin(){
        logger.info("Test: Enhanced Login with LLM");
         test.info("Test: Enhanced Login with LLM");
        try {
            loginPage.navigateToLoginPage();
            loginPage.login("swati123beh@gmail.com","Test@123");
            if(!(loginPage.validateLogin()))
            {
               logger.error("Login validation failed");
               test.info("Login validation failed");
            }              
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
