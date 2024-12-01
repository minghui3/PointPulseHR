package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver; 
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

public class ApplyLeave {
    private WebDriver driver;
    @Before
    public void initializeWebDriver(){
        driver = DriverManager.getDriver();
    }

    @Given("I am on the leave form page")
    public void i_am_on_the_leave_form_page(){
        driver.get("http://localhost:3000/leave-form.html");
    }
    @And("I select {string} as the type")
    public void i_select_as_the_leave_type(String leaveType){
        WebElement leaveDropdown = driver.findElement(By.xpath("/html/body/div[2]/div[2]/main/section/form/div[1]/select"));
        Select select = new Select(leaveDropdown);
        select.selectByVisibleText(leaveType);
    }
    @And("I select {string} as the approver")
    public void i_select_as_the_approver(String approverName){
        WebElement approverDropdown = driver.findElement(By.xpath("//select[@id='approver']"));
        new Select(approverDropdown).selectByVisibleText(approverName);
    }   
    @And("I enter {string} as the start date")
    public void i_enter_as_the_start_date(String startDate) {
        WebElement startDateInput = driver.findElement(By.xpath("//input[@id='start-date']"));
        // startDateInput.click();
        startDateInput.sendKeys(startDate);
    }
    @And("I enter {string} as the end date")
    public void i_enter_as_the_end_date(String endDate) {
        WebElement endDateInput = driver.findElement(By.xpath("//input[@id='end-date']"));
        endDateInput.sendKeys(endDate);
    }
    @And("I enter {string} as the reason")
    public void i_enter_as_the_reason_for_leave(String reason) {
        driver.findElement(By.id("reason")).sendKeys(reason);
    }
    @When("I click on the apply button")
    public void i_click_on_the_apply_button() {
        driver.findElement(By.xpath("/html/body/div[2]/div[2]/main/section/form/div[7]/button[2]")).click();
    }
    @Then("I should see a submission confirmation")
    public void i_should_see_a_submission_confirmation(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement overlay = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overlay")));
        assertTrue(overlay.isDisplayed());
    }

    @After
    public void closeBrowser(){
        DriverManager.quitDriver();
    }
}
