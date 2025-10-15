package org.example.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class BrokenLinks {
    private static final Logger log = LoggerFactory.getLogger(BrokenLinks.class);
    private static WebDriver driver;

    public static void main(String[] args) throws IOException {
        driver = new ChromeDriver();
        List<WebElement> anchorTags = driver.findElements(By.id("a"));
        for (WebElement we : anchorTags) {
            String href = we.getAttribute("href");
            if (Objects.nonNull(href)) {
                int statusCode = checkStatusOfLinks(href);
                if (statusCode >= 400) {
                    log.info("Link is Broken: {}", href);
                }
            }

        }

        WebElement until = new FluentWait<>(new ChromeDriver())
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(Exception.class)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("")));
    }

    private static int checkStatusOfLinks(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getResponseCode();
    }
}
