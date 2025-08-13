package com.example.e2e.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
        driver.manage().window().maximize();
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

    @Test
    public void editTeamAndValidateOutputAndPlayerLink() {
        driver.get(baseUrl + "/teams");

        // Wait until the table has at least one team
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")));

        // Locate the first row’s Edit button
        WebElement firstEditBtn = driver.findElement(By.xpath("//table/tbody/tr[1]//button[contains(text(), 'Edit')]"));
        firstEditBtn.click();

        // Wait until the form’s name input is pre-filled
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[placeholder='Name']")));
        WebElement cityInput = driver.findElement(By.cssSelector("input[placeholder='City']"));

        // Clear and update values
        nameInput.clear();
        nameInput.sendKeys("Arsenal FC");
        cityInput.clear();
        cityInput.sendKeys("London, UK");

        // Click Update
        WebElement updateBtn = driver.findElement(By.xpath("//button[normalize-space()='Update']"));
        updateBtn.click();

        // Wait for table to refresh with updated name & city
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table/tbody/tr[1]/td[2]"), "Arsenal FC"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.xpath("//table/tbody/tr[1]/td[3]"), "London, UK"));

        // Assertions on Teams page
        String updatedName = driver.findElement(By.xpath("//table/tbody/tr[1]/td[2]")).getText();
        String updatedCity = driver.findElement(By.xpath("//table/tbody/tr[1]/td[3]")).getText();
        Assert.assertEquals(updatedName, "Arsenal FC");
        Assert.assertEquals(updatedCity, "London, UK");

        // ---------- Navigate to Players page ----------
        driver.get(baseUrl + "/players");

        // Wait for players table to be present
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")));

        // Find any player row where the team column matches updatedName
        boolean playerHasUpdatedTeam = driver.findElements(By.xpath("//table/tbody/tr/td[contains(text(), 'Arsenal FC')]"))
                .size() > 0;

        Assert.assertTrue(playerHasUpdatedTeam,
                "At least one player should have team name updated to 'Arsenal FC'");
    }


}
