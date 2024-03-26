package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstructorControllerAddFinalGradesSystemTest {

    public static final String CHROME_DRIVER_FILE_LOCATION =
            //"C:/chromedriver_win32/chromedriver.exe";
            "D:/Documents/chromedriver-win64/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    private final String TESTGRADE = "F";
    private final String YEAR = "2024";
    private final String SEMESTER = "Spring";
    private final String SECTION = "8";

    // these tests assume that there is at least 1 student enrolled Section No 8 for Spring 2024
    //and that each student is being assigned a grade, as database does not allow for saving an empty grade
    // and that Grade is the 5th column in the table in EnrollmentsView component of the front end

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

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

    //instructor adds grades for all students
    @Test
    public void systemTestAddFinalGrades() throws Exception {

        //to store oldGrades
        List<String> oldgList = new ArrayList<>();

        //sendkeys to id year 2024
        WebElement we = driver.findElement(By.id("year"));
        we.sendKeys(YEAR);

        //sendkeys to id semester
        driver.findElement(By.id("semester")).sendKeys(SEMESTER);
        //we.sendKeys("Spring");

        //click id showSections
        driver.findElement(By.id("showSections")).click();
        //wait...
        Thread.sleep(2000);

        //find table row with first td SecNo 8
        WebElement rowSec = driver.findElement(By.xpath("//tr[td='8']"));
        //find hrefs on that row
        List<WebElement> links = rowSec.findElements(By.tagName("a"));
        //click first href for that row
        links.get(0).click();
        //wait..
        Thread.sleep(SLEEP_DURATION);

        //navigate to enrollments Table
        WebElement enrllmntsTable = driver.findElement(By.id("enrollmentsTable"));

        //get all the rows in id enrollmentsTable
        WebElement enrllmntsTableBody = enrllmntsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = enrllmntsTableBody.findElements(By.tagName("tr"));

        //for each row in enrollmentsTable
        for (WebElement row : rows) {

            //naigate to grade cell
            WebElement gradeInput = row.findElement(By.xpath(".//td[5]/input"));
            String oldGrade = gradeInput.getAttribute("value");

            //save the old grades in Array List
            oldgList.add(oldGrade);

            //enter new grade
            gradeInput.clear();
            gradeInput.sendKeys(TESTGRADE); //everybody fails!
        }

        //find button id "saveGrades" and click it
        driver.findElement(By.id("saveGrades")).click();

        Thread.sleep(SLEEP_DURATION);

        //Confirm that we received success message and the grades are changed
        //check for saved message "Enrollments saved"
        WebElement msgText = driver.findElement(By.id("enrollmentsMessage"));
        String resultMessage = msgText.getText();
        assertEquals("Enrollments saved", resultMessage);

        //confirm grades are changed
        for (WebElement row :rows) {

            //get text input field
            WebElement gradeInput = row.findElement(By.xpath(".//td[5]/input"));
            //save value of text input field in changedGrade
            String changedGrade = gradeInput.getAttribute("value");

            //confirm it holds test grade
            assertEquals(TESTGRADE, changedGrade);
        }

        Thread.sleep(SLEEP_DURATION);

        //return database to original state
        //iterate through original grades
        for(int i=0; i<oldgList.size(); i++) {

            //get grade cell for each row
            WebElement currRow = rows.get(i);
            WebElement gradeInput = currRow.findElement(By.xpath(".//td[5]/input"));

            // clear and re-enter original grade
            gradeInput.clear();
            gradeInput.sendKeys(oldgList.get(i));

        }
        //find button id "saveGrades" and click it
        driver.findElement(By.id("saveGrades")).click();

        Thread.sleep(SLEEP_DURATION);

        //Confirm that we received success message and the original grades have been saved
        WebElement restoredMsgText = driver.findElement(By.id("enrollmentsMessage"));
        String restoredMessage = restoredMsgText.getText();
        assertEquals("Enrollments saved", restoredMessage);

        //confirm grades are changed back
        for(int i=0; i<oldgList.size(); i++) {

            //for each original grade get row and value in grade cell
            WebElement currRow = rows.get(i);
            WebElement gradeInput = currRow.findElement(By.xpath(".//td[5]/input"));
            String restoredGrade = gradeInput.getAttribute("value");

            //confirm it holds original grade again
            assertEquals(oldgList.get(i), restoredGrade);

        }
    }
}
