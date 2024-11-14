package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import junit.framework.AssertionFailedError;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By; 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
 
public class LoginSteps 
{ 
    private WebDriver driver;

    @Before
    public void initializeWebDriver() {
        driver = DriverManager.getDriver();
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        driver.get("http://localhost:3000");
    }
    

    @And("I fill in the email as {string}")
    public void i_fill_in_the_email_as(String email) {
        driver.findElement(By.id("email")).sendKeys(email);
    }

    @And("I fill in the password as {string}") 
    public void i_fill_in_the_password_as(String password) {
        driver.findElement(By.id("password")).sendKeys(password);
    }

    @When("I click on the login button") 
    public void i_click_on_the_login_button() {
        driver.findElement(By.cssSelector("button")).click();

    }

    @Then("I see the alert {string}")
    public void i_see_the_alert(String message) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2)); // wait for up to 10 seconds
        wait.until(ExpectedConditions.alertIsPresent());
        
        String alertText = driver.switchTo().alert().getText();
        try {
            assertEquals(alertText, message);
        } catch (AssertionFailedError err) {
            err.printStackTrace();
        }
    }
    @And("I accept the alert")
    public void i_accept_the_alert() {
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }
    @After
    public void closeBrowser(){
        DriverManager.quitDriver();
    } 
}