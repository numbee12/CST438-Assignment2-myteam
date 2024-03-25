package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstructorControllerAddFinalGradesSystemTest {

    public static final String CHROME_DRIVER_FILE_LOCATION =
            //"C:/chromedriver_win32/chromedriver.exe";
            "D:/Documents/chromedriver-win64/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

    // these tests assume that there is at least 1 student enrolled Section No 8 for Spring 2024
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

    @Test
    public void systemTestAddFinalGrades() throws Exception {
        //add grades for all students

        //sendkeys to id year 2024
        WebElement we = driver.findElement(By.id("year"));
        we.sendKeys("2024");

        //sendkeys to id semester
        driver.findElement(By.id("semester")).sendKeys("Spring");
        //we.sendKeys("Spring");

        //click id showSections
        driver.findElement(By.id("showSections")).click();
        //wait...
        Thread.sleep(SLEEP_DURATION);

        //find table row with first td SecNo 8
        WebElement rowSec8 = driver.findElement(By.xpath("//tr[td='8']"));
        //find hrefs on that row
        List<WebElement> links = rowSec8.findElements(By.tagName("a"));
        //click first href for that row
        links.get(0).click();
        //wait..
        Thread.sleep(SLEEP_DURATION);

        //get all the headers (thead tag) in enrollmentsTable  --maybe not

        WebElement enrllmntsTable = driver.findElement(By.id("enrollmentsTable"));
        //retrieve the number of the column (index of the array of headers) that =="Grade"
//        List<WebElement> colHeadings = enrllmntsTable.findElements(By.tagName("th"));
//        //get all the rows in id enrollmentsTable ?
        WebElement enrllmntsTableBody = enrllmntsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = enrllmntsTableBody.findElements(By.tagName("tr"));
        //for each row in enrollmentsTable
        for (WebElement row : rows) {
            //in column number that Grade was in
            WebElement cell = row.findElement(By.xpath("//tr/td[5]"));
            ///html/body/div/div/div/table/tbody/tr/td[5]/input

            //enter a String Letter Grade
            WebElement gradeInput = cell.findElement(By.tagName("input"));

            //add code here to retrieve and save oldGrade first so I can put it back after

            gradeInput.clear();
            gradeInput.sendKeys("A"); //everybody gets an A!
        }

        driver.findElement(By.id("saveGrades")).click();

        Thread.sleep(5000);

        //put back to beginning

    //find button id "saveGrades"
    //click it

    //check that the grades are saved


}
