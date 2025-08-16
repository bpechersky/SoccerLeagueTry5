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
    @Test(priority = 1)
    public void createsMatchAndShowsPopulatedNames() {
        go("/matches");

        // Wait for form to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form")));

        // Get the two <select> elements (Home, Away)
        List<WebElement> selects = driver.findElements(By.tagName("select"));
        if (selects.size() < 2) {
            throw new IllegalStateException("Expected two team dropdowns");
        }

        Select homeSel = new Select(selects.get(0));
        Select awaySel = new Select(selects.get(1));

        Random rnd = new Random();

        // Pick random Home team (skip placeholder at index 0)
        int homeIndex = getRandomIndex(homeSel, rnd, -1);
        homeSel.selectByIndex(homeIndex);
        String homeTeam = homeSel.getOptions().get(homeIndex).getText().trim();

        // Pick random Away team (different from home)
        int awayIndex = getRandomIndex(awaySel, rnd, homeIndex);
        awaySel.selectByIndex(awayIndex);
        String awayTeam = awaySel.getOptions().get(awayIndex).getText().trim();

        // Random scores
        int homeScore = rnd.nextInt(5) + 1;
        int awayScore = rnd.nextInt(5) + 1;

        driver.findElement(By.cssSelector("input[placeholder='Home Score']"))
                .sendKeys(String.valueOf(homeScore));
        driver.findElement(By.cssSelector("input[placeholder='Away Score']"))
                .sendKeys(String.valueOf(awayScore));

        // Date in yyyy-MM-dd
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        WebElement dateInput = driver.findElement(By.cssSelector("input[type='date']"));
        dateInput.click();
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a")); // select all
        dateInput.sendKeys(today);                         // type date
        dateInput.sendKeys(Keys.TAB);                      // blur to trigger change



        // Submit
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Validate in table
        waitForTextInTable(homeTeam);
        waitForTextInTable(awayTeam);

        String table = driver.findElement(By.cssSelector("table")).getText();
        assertTrue(table.contains(homeTeam), "Expected home team in matches table");
        assertTrue(table.contains(awayTeam), "Expected away team in matches table");
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



}
