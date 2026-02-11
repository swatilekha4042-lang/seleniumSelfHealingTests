package com.eaapp.tests.pages;

import com.eaapp.tests.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    // Locators for Rahul Shetty Academy Practice login page
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("signInBtn");
    private final By errorMessage = By.cssSelector("div.alert-danger");
    private final By usernameValidation = By.xpath("//input[@id='username']/following-sibling::*[contains(@class, 'error')]");
    private final By passwordValidation = By.xpath("//input[@id='password']/following-sibling::*[contains(@class, 'error')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // Navigation
    public void navigateToLoginPage() {
        navigateTo(config.getBaseUrl());
    }

    // Actions
    public void enterUsername(String username) {
        sendKeys(usernameInput, username);
    }

    public void enterPassword(String password) {
        sendKeys(passwordInput, password);
    }

    public void clickLoginButton() {
        click(loginButton);
    }


    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    // Verifications
    public boolean isLoginPageDisplayed() {
        return isElementDisplayed(usernameInput) &&
               isElementDisplayed(passwordInput) &&
               isElementDisplayed(loginButton);
    }

    public boolean isUsernameFieldDisplayed() {
        return isElementDisplayed(usernameInput);
    }

    public boolean isPasswordFieldDisplayed() {
        return isElementDisplayed(passwordInput);
    }

    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(loginButton);
    }


    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        try {
            return getText(errorMessage);
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isUsernameValidationDisplayed() {
        return isElementDisplayed(usernameValidation);
    }

    public boolean isPasswordValidationDisplayed() {
        return isElementDisplayed(passwordValidation);
    }

    public String getUsernameValidationMessage() {
        try {
            return getText(usernameValidation);
        } catch (Exception e) {
            return "";
        }
    }

    public String getPasswordValidationMessage() {
        try {
            return getText(passwordValidation);
        } catch (Exception e) {
            return "";
        }
    }

    public String getPageSource() {
        return driver.getPageSource();
    }


}
