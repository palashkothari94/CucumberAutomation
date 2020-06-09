package stepDefinitions;

import java.util.List;

import org.junit.runner.RunWith;
import org.testng.annotations.BeforeClass;

import functions.Environment;
import functions.Reporter;
import functions.SCALE;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
public class stepDefinitions {
	
    SCALE SW = new SCALE();
    String receiptID = SW.RandomString(3).toUpperCase();

    @BeforeClass
    public void StartTest() {
        Environment.Tower = "Scale";
        Reporter.StartTest();
        Environment.SetBrowserToUse("GC");
        SW.LaunchBrowser(Environment.Scale);
    }

    @Given("^validate the browser$")
    public void validate_the_browser() throws Throwable {
        System.out.println("validate browser");
        
    }

    @When("^Browser is triggered$")
    public void browser_is_triggered() throws Throwable {
       System.out.println("browser is triggered");
    }

    @Then("^check if browser is displayed$")
    public void check_if_browser_is_displayed() throws Throwable {
        System.out.println("browser displayed");
    }
    
	@Given("^User is on BM Landing Page$")
	public void user_is_on_bm_landing_page() throws Throwable {
		System.out.println("navigate to url");
	}


	@When("^User login into application with user name and password$")
	public void user_login_into_application_with_user_name_and_password() throws Throwable {
		System.out.println("logged in successfully");
	}

	@Then("^Home Page is populated$")
	public void home_page_is_populated() throws Throwable {
		System.out.println("validated homepage");
	}

	@When("^User login into application with \"([^\"]*)\" and \"([^\"]*)\"$")
	public void user_login_into_application_with_something_and_something(String strArg1, String strArg2)throws Throwable {
		System.out.println(strArg1);
		System.out.println(strArg2);
		
	}

    @And("^Transation Log is displayed is \"([^\"]*)\"$")
    public void transation_log_is_displayed_is_something(String strArg1) throws Throwable {
        System.out.println(strArg1);
    }
    
    @When("^signup with following details$")
    public void signup_with_following_details(DataTable data) throws Throwable {
    		
    	List<List<String>> elements = data.asLists();
    		System.out.println(elements.get(0).get(1));
    	}
    
    @When("^User login in to application with (.+) and (.+)$")
    public void user_login_into_application_with_and(String username, String password) throws Throwable {
        System.out.println(username);
        System.out.println(password);
    }

}