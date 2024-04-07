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

public class StudentEnrollmentSectionSystemTest {

    public static String CHROME_DRIVER_FILE_LOCATION;
            //"C:/chromedriver_win32/chromedriver.exe";
           // "~/Users/sukhdeepmalhi/Desktop/chromedriver-mac-arm64/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.

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
    public void systemTestStudentEnroll() throws Exception {

        //Selects Enroll In Class
        driver.findElement(By.cssSelector("#root > div > nav > a:nth-child(3)")).click();
        Thread.sleep(SLEEP_DURATION);
        //Selects ENROLL
        driver.findElement(By.cssSelector("#root > div > table > tbody > tr:nth-child(1) > td:nth-child(9) > button")).click();
        Thread.sleep(SLEEP_DURATION);
        //Selects Enroll to Confirm
        driver.findElement(By.cssSelector("#react-confirm-alert > div > div > div > div > button:nth-child(1)")).click();
        Thread.sleep(SLEEP_DURATION);

        //Confirms student has succesfully enrolled
        String confirmMessage = driver.findElement(By.cssSelector("#root > div > h4")).getText();
        assertEquals("You have enrolled in Section: 6", confirmMessage);
        Thread.sleep(SLEEP_DURATION);


    }

}