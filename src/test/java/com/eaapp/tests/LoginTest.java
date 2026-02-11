package com.eaapp.tests;

import com.eaapp.tests.config.TestConfiguration;
import com.eaapp.tests.core.WebDriverFactory;
import com.eaapp.tests.pages.LoginPage;
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

    @BeforeClass
    public void setUp() {
        logger.info("========== Starting Test ==========");
        WebDriverFactory factory = new WebDriverFactory();
        driver = factory.createDriver();
        loginPage = new LoginPage(driver);
        config = TestConfiguration.getInstance();
        logger.info("WebDriver initialized successfully");
        llmClient = new LLMClient();
    }

    @AfterClass
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                logger.info("WebDriver closed successfully");
            }
        } catch (Exception e) {
            logger.error("Error closing WebDriver: " + e.getMessage());
        }
        logger.info("========== Test Complete ==========\n");
    }

    //@Test(description = "User logs in with valid credentials")
    public void testUserLoginWithValidCredentials() {
        logger.info("Test: User logs in with valid credentials");
        
        loginPage.navigateToLoginPage();
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
        Assert.assertTrue(loginPage.isLoginButtonDisplayed());

        loginPage.enterUsername(config.getValidUsername());
        loginPage.enterPassword(config.getValidPassword());
        loginPage.clickLoginButton();
           
        // Add assertion for successful login
        //Assert.assertTrue(loginPage.isTitleDisplayed());
        logger.info("Login test passed");
    }

    //@Test(description = "Login with invalid credentials shows error")
    public void testLoginWithInvalidCredentials() {
        logger.info("Test: Login with invalid credentials");
        
        loginPage.navigateToLoginPage();
        loginPage.enterUsername("invaliduser");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLoginButton();
        
        // Add assertion for error message
        logger.info("Invalid login test passed");
    }

    @Test(description = "Test enhanced login with LLM locator suggestion")
    public void testEnhancedLogin() throws Exception {
        logger.info("Test: Enhanced Login with LLM");
        
        loginPage.navigateToLoginPage();
        // By element = By.id("signInBtns");
        //SelfHealingLocators selfHealing=new SelfHealingLocators(driver, element);
        //WebElement aiElement=selfHealing.findElementCustom();
        //aiElement.click();
        loginPage.login("test","test");

    }
}
