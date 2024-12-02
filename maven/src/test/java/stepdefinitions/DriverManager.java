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
            String hubUrl = "http://localhost:4444/wd/hub";  // URL of the Selenium Grid Hub

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
                    edgeOptions.addArguments("--headless");

                    driver = new RemoteWebDriver(new URL(hubUrl), edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }

            // Set timeouts
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);  // Set page load timeout
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);  // Set implicit wait timeout
            driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);  // Set script timeout

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
