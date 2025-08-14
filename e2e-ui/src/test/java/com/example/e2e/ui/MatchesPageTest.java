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

    @Test(priority = 1)
    public void createsMatchAndShowsPopulatedNames() {
        // Make sure at least two teams exist
        ensureTeam("Arsenal", "London");
        ensureTeam("Liverpool", "Liverpool");

        go("/matches");

        // Locate dropdowns
        Select homeSel = new Select(driver.findElements(By.tagName("select")).get(0));
        Select awaySel = new Select(driver.findElements(By.tagName("select")).get(1));

        // Pick first valid home team
        String homeTeam = pickFirstRealTeam(homeSel, null);
        // Pick first valid away team that is different from home team
        String awayTeam = pickFirstRealTeam(awaySel, homeTeam);

        // Fill scores and date
        driver.findElement(By.cssSelector("input[placeholder='Home Score']")).sendKeys("2");
        driver.findElement(By.cssSelector("input[placeholder='Away Score']")).sendKeys("1");
        driver.findElement(By.cssSelector("input[type='date']")).sendKeys(LocalDate.now().toString());

        // Submit match form
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for table to update
        waitForTextInTable(homeTeam);
        waitForTextInTable(awayTeam);

        // Validate in table text
        String table = driver.findElement(By.cssSelector("table")).getText();
        assertTrue(table.contains(homeTeam), "Expected home team shown in matches table");
        assertTrue(table.contains(awayTeam), "Expected away team shown in matches table");
    }

    /**
     * Selects and returns the first "real" team option, skipping placeholders and optionally avoiding one team.
     */
    private String pickFirstRealTeam(Select select, String avoidTeam) {
        for (WebElement opt : select.getOptions()) {
            String text = opt.getText() == null ? "" : opt.getText().trim();
            String lower = text.toLowerCase();

            boolean placeholder =
                    text.isEmpty() ||
                            lower.contains("select") ||
                            lower.contains("choose") ||
                            lower.contains("none") ||
                            lower.contains("no team") ||
                            lower.equals("-") ||
                            lower.equals("—");

            if (!placeholder && (avoidTeam == null || !text.equals(avoidTeam))) {
                select.selectByVisibleText(text);
                return text;
            }
        }
        throw new IllegalStateException("No valid team found for dropdown");
    }
}
