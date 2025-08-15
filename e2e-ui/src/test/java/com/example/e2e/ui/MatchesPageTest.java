package com.example.e2e.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

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

    @Test(priority = 2)
    public void editMatchAndChangeTeamsScoresDate() {
        go("/matches");

        // Wait for table rows
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")));

        // Click Edit on first match row
        WebElement firstRow = driver.findElement(By.cssSelector("table tbody tr"));
        firstRow.findElement(By.xpath(".//button[normalize-space()='Edit']")).click();

        // Find the edit form that appears
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form")));

        // Locate home and away <select> elements in this form
        List<WebElement> selects = form.findElements(By.tagName("select"));
        if (selects.size() < 2) {
            throw new IllegalStateException("Expected 2 team dropdowns in edit form, found: " + selects.size());
        }

        Select homeSel = new Select(selects.get(0));
        Select awaySel = new Select(selects.get(1));

        Random random = new Random();

        // Pick random home team (skip placeholder at index 0)
        int homeIndex = getRandomIndex(homeSel, random, -1);
        homeSel.selectByIndex(homeIndex);
        String newHomeTeam = homeSel.getOptions().get(homeIndex).getText().trim();

        // Pick random away team, different from home team
        int awayIndex = getRandomIndex(awaySel, random, homeIndex);
        awaySel.selectByIndex(awayIndex);
        String newAwayTeam = awaySel.getOptions().get(awayIndex).getText().trim();

        // Generate random scores between 1 and 5
        int homeScore = random.nextInt(5) + 1;
        int awayScore = random.nextInt(5) + 1;

        // Update scores
        WebElement homeScoreInput = form.findElement(By.cssSelector("input[placeholder='Home Score']"));
        WebElement awayScoreInput = form.findElement(By.cssSelector("input[placeholder='Away Score']"));
        homeScoreInput.clear();
        homeScoreInput.sendKeys(String.valueOf(homeScore));
        awayScoreInput.clear();
        awayScoreInput.sendKeys(String.valueOf(awayScore));

        // Update date using JS to avoid 5-digit year issue
        WebElement dateInput = form.findElement(By.cssSelector("input[type='date']"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String newDate = LocalDate.now().minusDays(2).format(dateFormatter);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", dateInput, newDate);

        // Click Update
        form.findElement(By.xpath(".//button[normalize-space()='Update']")).click();

        // Validate in table
        waitForTextInTable(newHomeTeam);
        waitForTextInTable(newAwayTeam);

        String tableText = driver.findElement(By.cssSelector("table")).getText();
        assertTrue(tableText.contains(newHomeTeam), "Home team should be updated");
        assertTrue(tableText.contains(newAwayTeam), "Away team should be updated");
        assertTrue(tableText.contains(String.valueOf(homeScore)), "Home score should be updated");
        assertTrue(tableText.contains(String.valueOf(awayScore)), "Away score should be updated");
    }

    /**
     * Returns a random valid index for the select, skipping placeholder (index 0)
     * and optionally skipping avoidIndex.
     */
    private int getRandomIndex(Select select, Random random, int avoidIndex) {
        int optionsCount = select.getOptions().size();
        if (optionsCount <= 1) {
            throw new IllegalStateException("Not enough team options to select from");
        }
        int index;
        do {
            index = random.nextInt(optionsCount - 1) + 1; // 1 to size-1
        } while (index == avoidIndex);
        return index;
    }


}
