package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By error = By.xpath("//h3[text()='Username and password do not match any user in this service']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String user, String pass) {
        wrapper.type(username, user);
        wrapper.type(password, pass);
        wrapper.click(loginButton);
    }

    public boolean isErrorDisplayed() {
        return wrapper.isDisplayed(error);
    }
}
