package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {

    public static String CHROME_DRIVER_FILE_LOCATION; // =

    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    // add selenium dependency to pom.xml

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        String OS = System.getProperty("os.name");
        String BasePath = "src" + File.separator
                + "test" + File.separator
                + "java" + File.separator
                + "com" + File.separator
                + "cst438" + File.separator
                + "ChromeDriver" + File.separator;
        StringBuilder sb = new StringBuilder(BasePath);
        if (OS.contains("Windows")) {
            sb.append("chromedriver.exe");
            CHROME_DRIVER_FILE_LOCATION = sb.toString();
        } else if (OS.contains("Mac")) {
            sb.append("chromedriver");
            CHROME_DRIVER_FILE_LOCATION = sb.toString();
        }

        // set properties required by Chrome Driver
        System.setProperty(
                "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(URL);
        // must have a short wait to allow time for the page to download
        Thread.sleep(SLEEP_DURATION);

    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void systemTestAddAssignment() throws Exception {
        // Add an assignment for:
        // Year: 2024
        // Semester: Spring
        // Course Id: cst438
        // Sec No: 10
        // Sec Id: 1
        // Title: Test Assignment
        // Due Date: 2024-05-16
        // Verify assignment shows on the list of assignments for:
        // Spring, 2024, cst438
        // Delete the assignment
        // Verify the assignment is gone

        // navigate to Assignments View
        try {
            WebElement e;
            // type in the year
            {
                e = driver.findElement(By.cssSelector("#year"));
                e.click();
                e.sendKeys("2024");
            }
            // type in the semester
            {
                e = driver.findElement(By.cssSelector("#semester"));
                e.click();
                e.sendKeys("Spring");
            }
            // click "show sections"
            {
                e = driver.findElement(By.cssSelector("a[href='/sections']"));
                e.click();
                Thread.sleep(SLEEP_DURATION);
            }
            // click assignments for secNo 10 course cst438
            {
                e = driver.findElement(By.cssSelector("#instructor-assignment-link-10"));
                e.click();
                Thread.sleep(SLEEP_DURATION);
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }

        // verify the assignment is not in the table of assignments
        try {
            while (true) {
                WebElement e = driver.findElement(By.xpath("//tr[td='Test Assignment']"));
                List<WebElement> buttons = e.findElements(By.tagName("button"));
                // delete is the second button
                assertEquals(3, buttons.size());
                buttons.get(1).click();
                Thread.sleep(SLEEP_DURATION);
                // find the YES to confirm button
                List<WebElement> confirmButtons = driver
                        .findElement(By.className("react-confirm-alert-button-group"))
                        .findElements(By.tagName("button"));
                assertEquals(2, confirmButtons.size());
                confirmButtons.get(0).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
            // the assignment is not in the table
        }
        // time to try adding an assignment
        try{
            WebElement e;
            // click add assignment
            {
                e = driver.findElement(By.cssSelector("#addAsgnmtButton"));
                e.click();
                Thread.sleep(SLEEP_DURATION);
            }
            // type in the title
            {
                e = driver.findElement(By.cssSelector("#addAsgnmtTitle"));
                e.click();
                e.sendKeys("Test Assignment");
            }

            // FIXME: This is a hard test to structure for
            // an end to end system test.
            // Because once the semester ends this test will fail.
            // This is because the backend checks that the due date
            // is within the semester's begin and end dates

            // type in the due date
            {
                e = driver.findElement(By.cssSelector("#addAsgnmtDueDate"));
                e.click();
                // use last day of semester since front end checks for a date in the future
                e.sendKeys("2024-05-16");
            }
            // click yes button
            {
                List<WebElement> buttons = driver.findElements(By.cssSelector("div[class^='MuiDialogActions'] > button"));
                assertEquals(2, buttons.size());
                buttons.get(1).click(); // click yes (closes dialog)
                Thread.sleep(SLEEP_DURATION);
            }
            // check return message
            {
                String message = driver.findElement(By.tagName("h4")).getText();
                assertEquals("Assignment successfully added", message);
            }
            // verify assignment shows on the list of assignments
            {
                e = driver.findElement(By.xpath("//tr[td='Test Assignment']"));
                List<WebElement> buttons = e.findElements(By.tagName("button"));
                // delete is the second button
                assertEquals(3, buttons.size());
                buttons.get(1).click();
                Thread.sleep(SLEEP_DURATION);
                // find the YES to confirm button
                List<WebElement> confirmButtons = driver
                        .findElement(By.className("react-confirm-alert-button-group"))
                        .findElements(By.tagName("button"));
                assertEquals(2, confirmButtons.size());
                confirmButtons.get(0).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // verify the assignment is not in the table of assignments
        assertThrows(NoSuchElementException.class, () -> {
            driver.findElement(By.xpath("//tr[td='Test Assignment']"));
        });

    }

    @Test
    public void systemTestAddAssignmentBadDate() throws Exception {
        // add code to test adding assignment with bad date
    }
}
