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

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

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

        // add code to test adding assignment
    }

   @Test
    public void systemTestAddAssignmentBadDate() throws Exception {
        // add code to test adding assignment with bad date
    }
}
