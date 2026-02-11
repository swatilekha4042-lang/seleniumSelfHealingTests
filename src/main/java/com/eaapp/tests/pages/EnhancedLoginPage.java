package com.eaapp.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaapp.tests.core.WebDriverFactory;
import com.eaapp.tests.utilities.LLMClient;

public class EnhancedLoginPage {
     private WebDriver driver;
     private final By usernameInput = By.id("username");
     private LoginPage loginPage;
     private LLMClient llmClient;

     public EnhancedLoginPage(WebDriver driver, LLMClient llmClient) {
          this.driver = driver;
          this.llmClient = llmClient;
          this.loginPage = new LoginPage(driver);
     }

     public String enhancedLogin() throws Exception {

          String pagesource = loginPage.getPageSource();
          String strategyString = usernameInput.toString();
          int separatorIndex = strategyString.indexOf(": ");

          String locType = strategyString.substring(0, separatorIndex);
          String locValue = strategyString.substring(separatorIndex + 1).trim();
          String response=null;

          String prompt = "The Selenium web element with locatorType: " + locType + " and locator value: " + locValue + " cannot be found on the page.\n"
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

          response = llmClient.callLLMForLocator(locType, locValue, prompt);
          return response;
          
     }
}
