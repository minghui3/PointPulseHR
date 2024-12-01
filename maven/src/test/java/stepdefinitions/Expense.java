package stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class Expense {
    private WebDriver driver;

    @Before
    public void initializeWebDriver(){
        driver = DriverManager.getDriver();
    }
    @Given("I am on the expense form page")
    public void i_am_on_the_expense_form_page(){
        driver.get("http://localhost:3000/expense-form.html");
    }
    @And("I enter {string} as the expense date")
    public void i_enter_as_the_expense_date(String date){
        WebElement expenseDateInput =  driver.findElement(By.id("date"));
        // expenseDateInput.click();
        expenseDateInput.sendKeys(date);
    }
    @And("I enter {string} as the expense amount")
    public void i_entered_as_the_expense_amount(String amount){
        WebElement expenseAmount = driver.findElement(By.id("amount"));
        expenseAmount.sendKeys(amount);
    }   
    @After
    public void closeBrowser(){
        DriverManager.quitDriver();
    }
}
