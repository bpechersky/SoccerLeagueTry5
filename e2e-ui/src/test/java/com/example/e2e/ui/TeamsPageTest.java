package com.example.e2e.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

import static org.testng.Assert.assertTrue;

public class TeamsPageTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = System.getProperty("UI_BASE_URL", "http://localhost:5173");
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void teamsListShowsArsenalAndLiverpool() {
        driver.get(baseUrl + "/teams");
        // Wait until table is rendered
        WebElement table = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table"))
        );
        String text = table.getText();
               // .toLowerCase();
        assertTrue(text.contains("Arsenal"), "Expected 'Arsenal' in teams table");
        assertTrue(text.contains("Liverpool"), "Expected 'Liverpool' in teams table");
        assertTrue(text.contains("Manchester City"));
        assertTrue(text.contains("Chelsea"));
    }
}
