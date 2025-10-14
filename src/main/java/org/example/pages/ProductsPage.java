// File: src/main/java/org/example/pages/ProductsPage.java
package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductsPage extends BasePage {

    private final By title = By.cssSelector(".title");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDisplayed() {
        return wrapper.isDisplayed(title);
    }
}
