package com.example.e2e.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public abstract class BaseUiTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeClass
    public void setupClass() { WebDriverManager.chromedriver().setup(); }

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = System.getProperty("UI_BASE_URL", "http://localhost:5173");
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() { if (driver != null) driver.quit(); }

    protected void go(String path) {
        driver.get(baseUrl + path);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
    }

    protected void waitForTextInTable(String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("table"), text));
    }
}
