package com.cst438.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/*
 * Professor's example of SectionController System Test
 */
public class InstrGradesAsgnmtUploadAllScoreSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver_win32/chromedriver.exe";
//            "D:/Documents/chromedriver-win64/chromedriver.exe";

    //public static final String CHROME_DRIVER_FILE_LOCATION =
    //        "~/chromedriver_macOS/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.


    // add selenium dependency to pom.xml

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

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

    //instructor grades an assignment and enters scores for all enrolled students
    // and uploads the scores
    @Test
    public void gradeAsgnmtEnterScoresAllEnrolled() throws Exception{
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        Thread.sleep(SLEEP_DURATION);

        WebElement we = driver.findElement(By.id("showSections"));
        we.click();
        Thread.sleep(SLEEP_DURATION);

        try{
            while(true){
                //Selects Assignment in Sections
                WebElement sectionRow = driver.findElement(By.xpath("//tr[td='Assignments']"));
                List<WebElement> sectionLinks = sectionRow.findElements(By.tagName("a"));
                assertEquals(2, sectionLinks.size());
                sectionLinks.get(1).click();
                Thread.sleep(SLEEP_DURATION);

                //Selects Grade in Assignments
                WebElement asgnmtRow = driver.findElement(By.id("sectionAssignments"));
                List<WebElement> gradeButton = asgnmtRow.findElements(By.id("assignmentGrades"));
                gradeButton.get(0).click();
                Thread.sleep(SLEEP_DURATION);


                //Selects each score field and creates a list of assignment scores
                WebElement assignments = driver.findElement(By.id("assignments"));
                List<WebElement> asgScores = assignments.findElements(By.name("score"));

                //Asserts that the 5 text field for score exist
                assertEquals(5, asgScores.size());

                //Iterates through each score field for all students enrolled in the assignment
                //Add score for each student and saves scores.
                Integer add =0;
                for(int i = 0; i < asgScores.size(); i++){
                    Integer inputScore = (63 + 6 * i) + add;

                    if(asgScores.get(i).getText().equals("")) {
                        asgScores.get(i).clear();
                        String inScore = inputScore.toString();
                        asgScores.get(i).sendKeys(inScore);
                        add=add+2;
                        Thread.sleep(SLEEP_DURATION);
                    }else{
                        add=add+3;
                        asgScores.get(i).click();
                        Thread.sleep(SLEEP_DURATION);
                    }
                }
                driver.findElement(By.id("scoreSave")).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch(NoSuchElementException e){
            //Does Nothing
        }
    }
}
