package org.example.utils;

import org.example.driver.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

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
            log.debug("Element not displayed: {}", locator);
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

    public void dragAndDrop(By source, By target) {
        try {
            WebElement src = waitForVisible(source);
            WebElement dest = waitForVisible(target);
            new Actions(driver).dragAndDrop(src, dest).perform();
            log.info("Dragged element {} onto {}", source, target);
        } catch (Exception e) {
            log.error("Drag and drop failed from {} to {}: {}", source, target, e.getMessage());
            throw e;
        }
    }

    public void dragAndDropByOffset(By source, int xOffset, int yOffset) {
        try {
            WebElement src = waitForVisible(source);
            new Actions(driver)
                    .clickAndHold(src)
                    .moveByOffset(xOffset, yOffset)
                    .release()
                    .perform();
            log.info("Dragged {} by offset x={} y={}", source, xOffset, yOffset);
        } catch (Exception e) {
            log.error("Drag and drop by offset failed: {}", e.getMessage());
            throw e;
        }
    }

    public void selectByText(By locator, String visibleText) {
        try {
            Select dropdown = new Select(waitForVisible(locator));
            dropdown.selectByVisibleText(visibleText);
            log.info("Selected '{}' from {}", visibleText, locator);
        } catch (Exception e) {
            log.error("Failed to select '{}' from {}: {}", visibleText, locator, e.getMessage());
            throw e;
        }
    }

    public void selectByValue(By locator, String value) {
        try {
            Select dropdown = new Select(waitForVisible(locator));
            dropdown.selectByValue(value);
            log.info("Selected value='{}' from {}", value, locator);
        } catch (Exception e) {
            log.error("Failed to select value='{}' from {}: {}", value, locator, e.getMessage());
            throw e;
        }
    }

    public void selectByIndex(By locator, int index) {
        try {
            Select dropdown = new Select(waitForVisible(locator));
            dropdown.selectByIndex(index);
            log.info("Selected index {} from {}", index, locator);
        } catch (Exception e) {
            log.error("Failed to select index {} from {}: {}", index, locator, e.getMessage());
            throw e;
        }
    }

    public void selectAll(By locator) {
        try {
            Select dropdown = new Select(waitForVisible(locator));
            if (dropdown.isMultiple()) {
                for (WebElement option : dropdown.getOptions()) {
                    dropdown.selectByVisibleText(option.getText());
                }
                log.info("Selected all options from {}", locator);
            } else {
                log.warn("Dropdown {} is not multi-select", locator);
            }
        } catch (Exception e) {
            log.error("Failed to select all options from {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public List<WebElement> getSelectedOptions(By locator) {
        Select dropdown = new Select(waitForVisible(locator));
        return dropdown.getAllSelectedOptions();
    }

    public void switchToWindowByTitle(String title) {
        String current = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(title)) {
                log.info("Switched to window with title: {}", title);
                return;
            }
        }
        driver.switchTo().window(current);
        throw new NoSuchWindowException("No window found with title: " + title);
    }

    public void switchToNewWindow() {
        String current = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(current)) {
                driver.switchTo().window(handle);
                log.info("Switched to new window");
                return;
            }
        }
        log.warn("No new window found to switch to");
    }

    public void closeCurrentAndSwitchBack() {
        String current = driver.getWindowHandle();
        driver.close();
        Set<String> handles = driver.getWindowHandles();
        if (!handles.isEmpty()) {
            driver.switchTo().window(handles.iterator().next());
            log.info("Closed window {} and switched back to main", current);
        }
    }

    public void acceptAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            log.info("Accepting alert with text: {}", alert.getText());
            alert.accept();
        } catch (TimeoutException e) {
            log.warn("No alert present to accept");
        }
    }

    public void dismissAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            log.info("Dismissing alert with text: {}", alert.getText());
            alert.dismiss();
        } catch (TimeoutException e) {
            log.warn("No alert present to dismiss");
        }
    }

    public String getAlertText() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            log.info("Alert text: {}", text);
            return text;
        } catch (TimeoutException e) {
            log.warn("No alert present");
            return null;
        }
    }

    public void switchToFrame(By locator) {
        try {
            WebElement frame = waitForVisible(locator);
            driver.switchTo().frame(frame);
            log.info("Switched to frame {}", locator);
        } catch (Exception e) {
            log.error("Failed to switch to frame {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public void switchToFrame(int index) {
        try {
            driver.switchTo().frame(index);
            log.info("Switched to frame index {}", index);
        } catch (Exception e) {
            log.error("Failed to switch to frame index {}: {}", index, e.getMessage());
            throw e;
        }
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        log.info("Switched to default content");
    }

    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        log.info("Switched to parent frame");
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

    public void scrollIntoView(By locator) {
        try {
            WebElement el = waitForVisible(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
            log.debug("Scrolled into view: {}", locator);
        } catch (Exception e) {
            log.warn("Failed to scroll into view {}: {}", locator, e.getMessage());
        }
    }

    //input[type=file]
    public void uploadFile(By locator, String absoluteFilePath) {
        try {
            WebElement input = waitForVisible(locator);
            input.sendKeys(absoluteFilePath);
            log.info("Uploaded file: {}", absoluteFilePath);
        } catch (Exception e) {
            log.error("File upload failed for {}: {}", locator, e.getMessage());
            throw e;
        }
    }

    public void doubleClick(By locator) {
        new Actions(driver).doubleClick(waitForVisible(locator)).perform();
        log.debug("Double-clicked on {}", locator);
    }

    public void rightClick(By locator) {
        new Actions(driver).contextClick(waitForVisible(locator)).perform();
        log.debug("Right-clicked on {}", locator);
    }

    public void waitForPageLoad(int timeDurationInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeDurationInSeconds))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
        log.debug("Page fully loaded");
    }

    public void waitForAjax(int timeDurationInSeconds) {
        new WebDriverWait(driver, Duration.ofSeconds(timeDurationInSeconds))
                .until(d -> (Boolean) ((JavascriptExecutor) d)
                        .executeScript("return (window.jQuery != null) && (jQuery.active === 0);"));
        log.debug("All jQuery AJAX requests completed");
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        log.debug("Scrolled to bottom of page");
    }

    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        log.debug("Scrolled to top of page");
    }

    private String safeLog(String s) {
        if (s == null) return "";
        if (s.toLowerCase().contains("pass") || s.toLowerCase().contains("pwd") || s.toLowerCase().contains("password")) {
            return "****";
        }
        return s.length() > 100 ? s.substring(0, 100) + "..." : s;
    }
}