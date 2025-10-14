package org.example.utils;

import org.example.driver.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class SeleniumWrappers {

    private static final Logger log = LoggerFactory.getLogger(SeleniumWrappers.class);
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SeleniumWrappers() {
        this.driver = DriverFactory.getDriver();
        long timeout = Math.max(5, Integer.parseInt(System.getProperty("selenium.timeout", "15")));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        this.wait.pollingEvery(Duration.ofMillis(300));
        this.wait.ignoring(NoSuchElementException.class);
    }

    public WebElement waitForVisible(By locator) {
        log.debug("Waiting for visibility of {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        log.debug("Waiting for clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void click(By locator) {
        try {
            waitForClickable(locator).click();
            log.info("Clicked {}", locator);
        } catch (Exception e) {
            log.error("Failed to click {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public void type(By locator, String text) {
        try {
            WebElement el = waitForVisible(locator);
            el.clear();
            el.sendKeys(text);
            log.info("Typed into {} value='{}'", locator, safeLog(text));
        } catch (Exception e) {
            log.error("Failed to type into {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public String getText(By locator) {
        try {
            String text = waitForVisible(locator).getText();
            log.debug("getText {} => {}", locator, text);
            return text;
        } catch (Exception e) {
            log.error("Failed to getText {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public boolean isDisplayed(By locator) {
        try {
            boolean displayed = waitForVisible(locator).isDisplayed();
            log.debug("isDisplayed {} => {}", locator, displayed);
            return displayed;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void moveTo(By locator) {
        try {
            WebElement el = waitForVisible(locator);
            new Actions(driver).moveToElement(el).perform();
            log.debug("moveTo {}", locator);
        } catch (Exception e) {
            log.warn("moveTo failed {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public void back() {
        driver.navigate().back();
        log.debug("navigate back");
    }

    public void forward() {
        driver.navigate().forward();
        log.debug("navigate forward");
    }

    public void refresh() {
        driver.navigate().refresh();
        log.debug("refresh page");
    }

    public void waitForTitle(String title) {
        wait.until(ExpectedConditions.titleIs(title));
    }

    private String safeLog(String s) {
        if (s == null) return "";
        if (s.toLowerCase().contains("pass") || s.toLowerCase().contains("pwd") || s.toLowerCase().contains("password")) {
            return "****";
        }
        return s.length() > 100 ? s.substring(0, 100) + "..." : s;
    }
}
