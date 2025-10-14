package org.example.driver;

import org.example.config.Config;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.time.Duration;

public final class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void setDriver(WebDriver driver) {
        tlDriver.set(driver);
    }

    public static void removeDriver() {
        WebDriver d = tlDriver.get();
        if (d != null) {
            try { d.quit(); } catch (Exception e) { logger.warn("Driver quit failed", e); }
        }
        tlDriver.remove();
    }

    public static void initDriver(String browser) {
        String gridUrl = Config.get("grid.url");
        WebDriver driver;
        try {
            if (gridUrl != null && !gridUrl.isEmpty()) {
                logger.info("Creating remote driver for browser: {} at grid {}", browser, gridUrl);
                Capabilities caps = createCapabilities(browser);
                driver = new RemoteWebDriver(new URL(gridUrl), caps);
            } else {
                logger.info("Creating local driver for browser: {}", browser);
                driver = createLocalDriver(browser);
            }
            int implicit = Config.getInt("implicit.wait", 10);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
            driver.manage().window().maximize();
            setDriver(driver);
        } catch (Exception e) {
            logger.error("Failed to initialize driver: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "firefox":
                FirefoxOptions fOpts = new FirefoxOptions();
                fOpts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                return new FirefoxDriver(fOpts);
            case "edge":
                EdgeOptions eOpts = new EdgeOptions();
                eOpts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                return new EdgeDriver(eOpts);
            case "chrome":
            default:
                ChromeOptions cOpts = new ChromeOptions();
                cOpts.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                return new ChromeDriver(cOpts);
        }
    }

    private static Capabilities createCapabilities(String browser) {
        switch (browser.toLowerCase()) {
            case "firefox":
                FirefoxOptions fOpts = new FirefoxOptions();
                fOpts.setCapability("platformName", Platform.ANY);
                return fOpts;
            case "edge":
                EdgeOptions eOpts = new EdgeOptions();
                eOpts.setCapability("platformName", Platform.ANY);
                return eOpts;
            case "chrome":
            default:
                ChromeOptions cOpts = new ChromeOptions();
                cOpts.setCapability("platformName", Platform.ANY);
                return cOpts;
        }
    }
}
