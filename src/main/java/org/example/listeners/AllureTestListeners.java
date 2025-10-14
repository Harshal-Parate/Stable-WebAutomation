package org.example.listeners;

import io.qameta.allure.Allure;
import org.example.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Objects;

import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class AllureTestListeners implements ITestListener, ISuiteListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureTestListeners.class);

    @Override
    public void onStart(ISuite suite) {
        LOGGER.info("Suite started: {}", suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        LOGGER.info("Suite finished: {}", suite.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info("Test context started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("Test context finished: {}", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("Test started: {}.{}()",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName());

        // test parameters as readable info in Allure
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            String joined = "Parameters: " + Arrays.stream(params)
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));
            Allure.addAttachment("Test Parameters", "text/plain", new ByteArrayInputStream(joined.getBytes(StandardCharsets.UTF_8)), "txt");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test succeeded: {}.{}()",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("Test skipped: {}.{}() - {}",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName(),
                readableThrowable(result.getThrowable()));

        // Attach skip reason
        if (result.getThrowable() != null) {
            Allure.addAttachment("Skip reason", "text/plain",
                    new ByteArrayInputStream(readableThrowable(result.getThrowable()).getBytes(StandardCharsets.UTF_8)),
                    "txt");
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        LOGGER.warn("Test failed but within success percentage: {}.{}()",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("Test failed: {}.{}() - {}",
                result.getMethod().getTestClass().getName(),
                result.getMethod().getMethodName(),
                readableThrowable(result.getThrowable()));

        // Attach throwable stacktrace
        if (result.getThrowable() != null) {
            Allure.addAttachment("Exception", "text/plain",
                    new ByteArrayInputStream(readableThrowable(result.getThrowable()).getBytes(StandardCharsets.UTF_8)),
                    "txt");
        }

        // Attach screenshot, page source and browser logs
        attachScreenshot("Failed test - " + result.getName());
        attachPageSource("Page source - " + result.getName());
        attachBrowserConsoleLogs("Browser console logs - " + result.getName());

        Allure.step("Test failed: " + result.getName());
    }

    private void attachScreenshot(String name) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            LOGGER.debug("No WebDriver available; skipping screenshot attachment.");
            return;
        }
        try {
            if (driver instanceof TakesScreenshot) {
                byte[] screenshotAs = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshotAs), "png");
                LOGGER.debug("Screenshot attached for {}", name);
            } else {
                LOGGER.warn("Driver does not support TakesScreenshot; skipping screenshot.");
            }
        } catch (WebDriverException wde) {
            LOGGER.warn("Failed to capture screenshot: {}", wde.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Unexpected error while attaching screenshot: {}", e.getMessage());
        }
    }

    private void attachPageSource(String name) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            LOGGER.debug("No WebDriver available; skipping page source attachment.");
            return;
        }
        try {
            String pageSource = driver.getPageSource();
            Allure.addAttachment(name, "text/html", new ByteArrayInputStream(pageSource.getBytes(StandardCharsets.UTF_8)), "html");
            LOGGER.debug("Page source attached for {}", name);
        } catch (WebDriverException wde) {
            LOGGER.warn("Failed to get page source: {}", wde.getMessage());
        } catch (Exception e) {
            LOGGER.warn("Unexpected error while attaching page source: {}", e.getMessage());
        }
    }

    private void attachBrowserConsoleLogs(String name) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            LOGGER.debug("No WebDriver available; skipping console logs.");
            return;
        }

        try {
            if (driver.manage() == null) {
                return;
            }
            try {
                List<LogEntry> entries = driver.manage().logs().get(LogType.BROWSER).getAll();
                if (entries != null && !entries.isEmpty()) {
                    String logs = entries.stream()
                            .map(le -> String.format("%s %s %s", le.getLevel(), le.getTimestamp(), le.getMessage()))
                            .collect(Collectors.joining(System.lineSeparator()));
                    Allure.addAttachment(name, "text/plain", new ByteArrayInputStream(logs.getBytes(StandardCharsets.UTF_8)), "txt");
                    LOGGER.debug("Browser logs attached for {}", name);
                } else {
                    LOGGER.debug("No browser console logs available for {}", name);
                }
            } catch (UnsupportedCommandException | IllegalArgumentException ex) {
                LOGGER.debug("Browser logs not supported by this driver: {}", ex.getMessage());
            }
        } catch (Exception e) {
            LOGGER.warn("Unexpected error while attaching browser logs: {}", e.getMessage());
        }
    }

    private static String readableThrowable(Throwable t) {
        if (t == null) return "null";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}


