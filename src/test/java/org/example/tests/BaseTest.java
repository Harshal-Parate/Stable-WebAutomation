package org.example.tests;

import org.example.config.Config;
import org.example.config.EnvironmentManager;
import org.example.driver.DriverFactory;

import org.example.utils.UserCredentials;
import org.example.utils.UsersLoader;
import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class BaseTest {

    protected WebDriver driver;

    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        DriverFactory.initDriver(browser);
        this.driver = DriverFactory.getDriver();
        this.driver.manage().window().maximize();
        String baseUrl = EnvironmentManager.fromString(Config.getOrDefault("env","qa")).getBaseUrl();
        this.driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.removeDriver();
    }

    public static UserCredentials getCreds(String user) {
        if (Objects.isNull(UsersLoader.getUserById(user))) {
            throw new RuntimeException("User not found: " + user);
        }
        return UsersLoader.getUserById(user);
    }

}