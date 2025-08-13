package com.example.e2e.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.assertTrue;

public class MatchesPageTest extends BaseUiTest {

    private void ensureTeam(String name, String city) {
        go("/teams");
        String tableText = driver.findElement(By.cssSelector("table")).getText().toLowerCase();
        if (!tableText.contains(name.toLowerCase())) {
            driver.findElement(By.cssSelector("input[placeholder='Name']")).sendKeys(name);
            driver.findElement(By.cssSelector("input[placeholder='City']")).sendKeys(city);
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            waitForTextInTable(name);
        }
    }

    @Test
    public void createsMatchAndShowsPopulatedNames() {
        // Make sure the teams exist first
        ensureTeam("Arsenal", "London");
        ensureTeam("Liverpool", "Liverpool");

        go("/matches");

        // Create a match via the form
        Select homeSel = new Select(driver.findElements(By.tagName("select")).get(0));
        Select awaySel = new Select(driver.findElements(By.tagName("select")).get(1));
        homeSel.selectByVisibleText("Arsenal");
        awaySel.selectByVisibleText("Liverpool");

        driver.findElement(By.cssSelector("input[placeholder='Home Score']")).sendKeys("2");
        driver.findElement(By.cssSelector("input[placeholder='Away Score']")).sendKeys("1");
        driver.findElement(By.cssSelector("input[type='date']")).sendKeys(LocalDate.now().toString());
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for table to include the row
        waitForTextInTable("Arsenal");
        waitForTextInTable("Liverpool");
        

        String table = driver.findElement(By.cssSelector("table")).getText();
        assertTrue(table.contains("Arsenal"), "Expected 'Arsenal' shown in matches table");
        assertTrue(table.contains("Liverpool"), "Expected 'Liverpool' shown in matches table");
    }
}
