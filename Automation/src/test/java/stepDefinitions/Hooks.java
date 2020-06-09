package stepDefinitions;

import Cucumber.Automation.Base;
import io.cucumber.java.*;

public class Hooks extends Base {

	@Before("@MobileTest")
	public void beforevalidation() {
		System.out.println("Mobile before Hook");
	}

	@After("@MobileTest")
	public void Aftervaldiation() {
		System.out.println("  After Mobile before hook");
	}

	@Before("@WebTest")
	public void beforeWebvaldiation() {
		System.out.println("Before Web  hook");
	}

	@After("@WebTest")
	public void AfterWebvaldiation() {
		System.out.println("  After Web before hook");
	}
	
	@After("@SeleniumTest")
	public void AfterSeleniumtest() {
		driver.close();
	}

}
