package org.example.pages;

import org.example.driver.DriverFactory;
import org.example.utils.SeleniumWrappers;
import org.openqa.selenium.WebDriver;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final SeleniumWrappers wrapper;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wrapper = new SeleniumWrappers();
    }

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wrapper = new SeleniumWrappers();
    }
}
