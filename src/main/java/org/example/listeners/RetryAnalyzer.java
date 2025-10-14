package org.example.listeners;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = Integer.parseInt(System.getProperty("retry.count"));

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            String message = String.format("Retrying test '%s' (attempt %d/%d)",
                    result.getName(), retryCount, MAX_RETRY_COUNT);
            logger.warn(message);
            try {
                Allure.step(message);
            } catch (Exception ignored) {
            }
            return true;
        }
        return false;
    }
}
