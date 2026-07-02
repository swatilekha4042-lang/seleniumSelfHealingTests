package com.eaapp.tests.pages;

import com.eaapp.tests.core.BasePage;
import com.eaapp.tests.utilities.SelfHealingLocators;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {
    // Locators for Rahul Shetty Academy Practice login page

    private final SelfHealingLocators usernameInput;
    private final SelfHealingLocators passwordInput;
    private final SelfHealingLocators loginButton;
    private final SelfHealingLocators homePageElement;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.usernameInput = new SelfHealingLocators(driver, By.id("userEmailss"));
        this.passwordInput = new SelfHealingLocators(driver, By.id("userPassword"));
        this.loginButton = new SelfHealingLocators(driver, By.id("login"));
        this.homePageElement = new SelfHealingLocators(driver, By.xpath("(//*[contains(@class, 'card-body')])[1]"));
    }

    // Navigation
    public void navigateToLoginPage() {
        navigateTo(config.getBaseUrl());
    }

    // Actions
    public WebElement enterUsername(String username) {
        WebElement usernameEle = usernameInput.findElementCustom();
        sendKeys(usernameEle, username);
        return usernameEle;
    }

    public void enterPassword(String password) {
        WebElement passEle = passwordInput.findElementCustom();
        sendKeys(passEle, password);
    }

    public void clickLoginButton() {
        WebElement loginBtnEle = loginButton.findElementCustom();
        waitForElementToBeClickable(loginBtnEle);
        click(loginBtnEle);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public boolean validateLogin() {
        WebElement homepageEle = homePageElement.findElementCustom();
        return isElementDisplayed(homepageEle);
    }

}
