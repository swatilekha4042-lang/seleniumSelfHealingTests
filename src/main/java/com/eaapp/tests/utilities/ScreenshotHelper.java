package com.eaapp.tests.utilities;

import com.eaapp.tests.config.TestConfiguration;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotHelper {
    private static final Logger logger = Logger.getLogger(ScreenshotHelper.class);

    public static void takeScreenshot(WebDriver driver, String testName) {
        takeScreenshot(driver, testName, "");
    }

    public static void takeScreenshot(WebDriver driver, String testName, String additionalInfo) {
        try {
            TestConfiguration config = TestConfiguration.getInstance();
            String screenshotDir = System.getProperty("user.dir") + File.separator + config.getScreenshotPath();

            // Create directory if it doesn't exist
            Files.createDirectories(Paths.get(screenshotDir));

            // Generate filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName;
            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                fileName = testName + "_" + additionalInfo + "_" + timestamp + ".png";
            } else {
                fileName = testName + "_" + timestamp + ".png";
            }

            String filePath = screenshotDir + File.separator + fileName;

            // Take screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), Paths.get(filePath));

            logger.info("Screenshot saved: " + filePath);
        } catch (IOException e) {
            logger.error("Failed to take screenshot: " + e.getMessage());
        }
    }
}
