// File: src/test/java/org/example/tests/LoginTests.java
package org.example.tests;

import org.example.pages.LoginPage;
import org.example.pages.ProductsPage;
import org.example.utils.UserCredentials;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    private LoginPage loginPage;
    private ProductsPage productsPage;
    private final UserCredentials user;

    public LoginTests() {
        user = getCreds("User1");
    }

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
    }

    @Test(description = "valid login")
    public void validLogin() {
        loginPage.login(user.getUsername(), user.getPassword());
        Assert.assertTrue(productsPage.isDisplayed(), "Products not displayed after login");
    }

    @Test(description = "invalid login")
    public void invalidLogin() {
        loginPage.login(user.getUsername(), "invalid");
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error should be visible for invalid login");
    }
}
