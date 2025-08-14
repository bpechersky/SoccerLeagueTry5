package com.example.e2e.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class PlayersPageCreateTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:5173/players"; // adjust if route differs

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void createNewPlayer_AssignRealTeam_AndVerifyTeamShown() {
        driver.get(BASE_URL);

        // Wait for players table (or at least page to settle)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody")));

        // Use unique player name to avoid collisions
        String playerName = "Test Player " + System.currentTimeMillis();
        String position = "Midfielder";

        // ---- Fill in form ----
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='Name']")));
        WebElement positionInput = driver.findElement(By.cssSelector("input[placeholder='Position']"));

        // Team dropdown (assumes single <select> on the form; tweak selector if needed)
        WebElement teamSelectEl = driver.findElement(By.tagName("select"));
        Select teamSelect = new Select(teamSelectEl);

        String chosenTeamText = pickFirstRealTeam(teamSelect);
        Assert.assertNotNull(chosenTeamText, "No valid team options found to assign to new player");

        // Fill fields
        nameInput.clear();
        nameInput.sendKeys(playerName);
        positionInput.clear();
        positionInput.sendKeys(position);

        // Submit
        WebElement addBtn = driver.findElement(By.xpath("//button[normalize-space()='Add']"));
        addBtn.click();

        // ---- Verify new player row appears with selected team ----
        By nameCellLocator = By.xpath("//table/tbody/tr/td[2][normalize-space()='" + playerName + "']");
        wait.until(ExpectedConditions.presenceOfElementLocated(nameCellLocator));

        WebElement rowNameCell = driver.findElement(nameCellLocator);
        WebElement row = rowNameCell.findElement(By.xpath("./ancestor::tr"));

        // Verify player name in table
        String actualName = row.findElement(By.xpath("./td[2]")).getText().trim();
        Assert.assertEquals(actualName, playerName, "Player name should match what was entered");

        // Assuming: 1=ID, 2=Name, 3=Position, 4=Team (adjust indices if your table differs)
        String actualPosition = row.findElement(By.xpath("./td[3]")).getText().trim();
        String actualTeam     = row.findElement(By.xpath("./td[4]")).getText().trim();

        Assert.assertEquals(actualPosition, position, "Position should match what was entered");
        Assert.assertEquals(actualTeam, chosenTeamText, "Team column should show the selected team");
    }

    /**
     * Picks the first "real" team option, skipping placeholders like:
     * - "No Team", "None", "Select", "Choose", "—"
     * and values that are blank/0/null/-1.
     * Returns the visible text and selects it in the dropdown.
     */
    private String pickFirstRealTeam(Select select) {
        List<WebElement> options = select.getOptions();
        for (WebElement opt : options) {
            String text = (opt.getText() == null ? "" : opt.getText().trim());
            String value = (opt.getAttribute("value") == null ? "" : opt.getAttribute("value").trim());
            String lower = text.toLowerCase();

            boolean isPlaceholderText =
                    text.isEmpty()
                            || lower.contains("no team")
                            || lower.contains("none")
                            || lower.contains("select")
                            || lower.contains("choose")
                            || lower.equals("-")
                            || lower.equals("—");

            boolean isPlaceholderValue =
                    value.isEmpty()
                            || value.equals("0")
                            || value.equals("-1")
                            || value.equalsIgnoreCase("null")
                            || value.equalsIgnoreCase("undefined");

            if (!isPlaceholderText && !isPlaceholderValue) {
                select.selectByVisibleText(text);
                return text;
            }
        }
        return null; // no valid option found
    }
    @Test
    public void editPlayer_updateNamePositionTeam_andValidate() {
        driver.get(BASE_URL);

        // Wait for table to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody")));

        // --- Choose a player row to edit (first data row) ---
        // If you want a specific player by name, replace with a locator targeting that name.
        WebElement firstRow = driver.findElement(By.xpath("//table/tbody/tr[1]"));
        String originalName = firstRow.findElement(By.xpath("./td[2]")).getText().trim();
        String originalTeam = firstRow.findElement(By.xpath("./td[4]")).getText().trim();

        // Click Edit in that row
        WebElement editBtn = firstRow.findElement(By.xpath(".//button[normalize-space()='Edit']"));
        editBtn.click();

        // Wait for pre-filled form
        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[placeholder='Name']")));
        WebElement positionInput = driver.findElement(By.cssSelector("input[placeholder='Position']"));
        WebElement teamSelectEl = driver.findElement(By.tagName("select"));
        Select teamSelect = new Select(teamSelectEl);

        // --- New values ---
        String newName = originalName + " (Edited)";
        String newPosition = "Attacking Midfielder"; // change to any valid position

        // Pick a different real team (skip placeholder-like options and avoid original team if possible)
        String newTeam = pickDifferentRealTeam(teamSelect, originalTeam);
        Assert.assertNotNull(newTeam, "Could not find an alternative valid team to select");

        // Fill the form with new values
        nameInput.clear();
        nameInput.sendKeys(newName);

        positionInput.clear();
        positionInput.sendKeys(newPosition);

        // Select new team
        teamSelect.selectByVisibleText(newTeam);

        // Click Update
        WebElement updateBtn = driver.findElement(By.xpath("//button[normalize-space()='Update']"));
        updateBtn.click();

        // --- Validate updated row ---
        // Locate the row by new player name
        By nameCellLocator = By.xpath("//table/tbody/tr/td[2][normalize-space()='" + newName + "']");
        wait.until(ExpectedConditions.presenceOfElementLocated(nameCellLocator));

        WebElement rowNameCell = driver.findElement(nameCellLocator);
        WebElement updatedRow = rowNameCell.findElement(By.xpath("./ancestor::tr"));

        String actualName = updatedRow.findElement(By.xpath("./td[2]")).getText().trim();
        String actualPosition = updatedRow.findElement(By.xpath("./td[3]")).getText().trim();
        String actualTeam = updatedRow.findElement(By.xpath("./td[4]")).getText().trim();

        Assert.assertEquals(actualName, newName, "Player name should be updated");
        Assert.assertEquals(actualPosition, newPosition, "Player position should be updated");
        Assert.assertEquals(actualTeam, newTeam, "Player team should reflect the newly selected team");
    }

    /**
     * Selects and returns the first valid team that's different from `avoidTeam`,
     * skipping placeholders like "No Team", "None", "Select", "Choose", empty, 0, -1, null, undefined, etc.
     */
    private String pickDifferentRealTeam(Select select, String avoidTeam) {
        List<WebElement> options = select.getOptions();
        for (WebElement opt : options) {
            String text = (opt.getText() == null ? "" : opt.getText().trim());
            String value = (opt.getAttribute("value") == null ? "" : opt.getAttribute("value").trim());
            String lower = text.toLowerCase();

            boolean placeholderText =
                    text.isEmpty()
                            || lower.contains("no team")
                            || lower.contains("none")
                            || lower.contains("select")
                            || lower.contains("choose")
                            || lower.equals("-")
                            || lower.equals("—");

            boolean placeholderValue =
                    value.isEmpty()
                            || value.equals("0")
                            || value.equals("-1")
                            || value.equalsIgnoreCase("null")
                            || value.equalsIgnoreCase("undefined");

            if (!placeholderText && !placeholderValue && !text.equals(avoidTeam)) {
                select.selectByVisibleText(text);
                return text;
            }
        }

        // Fallback: if no different team found, at least pick a valid one (even if same as current)
        for (WebElement opt : options) {
            String text = (opt.getText() == null ? "" : opt.getText().trim());
            String value = (opt.getAttribute("value") == null ? "" : opt.getAttribute("value").trim());
            String lower = text.toLowerCase();

            boolean placeholderText =
                    text.isEmpty()
                            || lower.contains("no team")
                            || lower.contains("none")
                            || lower.contains("select")
                            || lower.contains("choose")
                            || lower.equals("-")
                            || lower.equals("—");

            boolean placeholderValue =
                    value.isEmpty()
                            || value.equals("0")
                            || value.equals("-1")
                            || value.equalsIgnoreCase("null")
                            || value.equalsIgnoreCase("undefined");

            if (!placeholderText && !placeholderValue) {
                select.selectByVisibleText(text);
                return text;
            }
        }
        return null;
    }
}
