package stepdefinitions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DriverManager {
    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    private static void initializeDriver() {
        // Get the browser type from the system property, default to Chrome
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        try {
            String hubUrl = "http://selenium-hub:4444/";  // URL of the Selenium Grid Hub

            switch (browser) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    driver = new RemoteWebDriver(new URL(hubUrl), chromeOptions);
                    break;

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    driver = new RemoteWebDriver(new URL(hubUrl), firefoxOptions);
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    driver = new RemoteWebDriver(new URL(hubUrl), edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
            

        } catch (Exception e) {
            System.out.println("Error initializing WebDriver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver", e);  // Re-throw exception for better handling
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
