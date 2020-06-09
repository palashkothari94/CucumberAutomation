package stepDefinitions;

import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Cucumber.Automation.Base;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import pageObjects.HomePage;


@RunWith(Cucumber.class)
public class Login {

	public WebDriver driver;
	HomePage h;
	//WebDriverWait wait = new WebDriverWait(driver, 40);

	@Given("User is on BM Landing page")
	public void user_is_on_BM_Landing_page() throws Throwable {		

		driver = Base.getDriver();
		
		driver.manage().window().maximize();
		Thread.sleep(5000);
		
		h= new HomePage (driver);
		h.getSearch().click();
		
		

	}

	@When("user wants to login")
	public void user_wants_to_login() throws Throwable {
		
		Thread.sleep(3000);
		WebElement Username = driver.findElement(By.xpath("//input[@id='userNameInput']"));
		Thread.sleep(3000);
		Username.click();
		Username.sendKeys("pkothari@ilsnetdev.com");
		WebElement Password = driver.findElement(By.xpath("//input[@id='passwordInput']"));
		Password.click();
		Password.sendKeys("Manh1234");
		driver.findElement(By.xpath("//span[@id='submitButton']")).click();	
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@class='fal fa-align-left toolbaricon button button-icon button-clear']"))).isDisplayed();
		//driver.findElement(By.xpath("//i[@class='fal fa-align-left toolbaricon button button-icon button-clear']")).click();

	}

	@Then("Home Page is Presented")
	public void home_Page_is_Presented() {
		// Write code here that turns the phrase above into concrete actions

	}

}
