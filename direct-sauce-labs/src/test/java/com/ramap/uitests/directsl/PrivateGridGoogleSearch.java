package com.ramap.uitests.directsl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PrivateGridGoogleSearch {

	private RemoteWebDriver driver = null;
	private static Logger logger = Logger.getAnonymousLogger();;

	public static void main(String [] args) {
		System.err.println("Usage");
		System.err.println("Make sure you have a properties file name uitests.properties in your home directory");
		System.err.println("Sample Properties file....");
		System.err.println();System.err.println();
		System.err.println("sauceUserName=xxxx");
		System.err.println("sauceAccessKey=xxxx");
		System.err.println("#Capabilities");
		System.err.println("browserName=chrome");
		System.err.println("platform=XP");
		System.err.println("version=43.0");
	}

	@BeforeMethod
	public void init() throws FileNotFoundException, IOException {
//		Run these command and make sure a Selenium grid is up and running before executing this test
//		docker pull elgalu/selenium
//		docker run -d --name=grid -p 4444:24444 -p 5900:25900 \
//		     -e TZ="US/Pacific" -v /dev/shm:/dev/shm --privileged elgalu/selenium
		
		String privateGridUrl = "http://localhost:4444/wd/hub";

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setBrowserName("chrome");

		System.setProperty("webdriver.chrome.driver", "/home/seluser/chromedriver");
		ChromeOptions options = new ChromeOptions();
		options.setBinary("/usr/bin/google-chrome-stable");
		options.addArguments("--headless");
		options.addArguments("--disable-gpu");
		options.addArguments("--start-maximized");

		caps.setCapability(ChromeOptions.CAPABILITY, options.toJson());
		
		driver = new RemoteWebDriver(new URL(privateGridUrl), caps);
		logger.info("Session Id: " + driver.getSessionId());
	}

	@Test
	public void testGoogle() {
		driver.get("https://www.google.com/");
		logger.info("title of page is: " + driver.getTitle());
		logger.info("Search for 'Intuit'");
		WebElement q = getElementByName(driver, "q");
		String searchQuery = "Intuit";
		q.sendKeys(searchQuery);
		WebElement searchButton = getElementByXPath(driver, "//*[@value=\"Search\"]");
		searchButton.click();
		// Wait for the results to come up
		@SuppressWarnings("unused")
		WebElement results = getElementByXPath(driver, "//*[@id=\"resultStats\"]");
		logger.info("title of page after search: " + driver.getTitle());
		Assert.assertEquals(driver.getTitle(), searchQuery + " - Google Search");
	}

	// @Test
	public void testSauceGuineaPig() {
		driver.get("https://saucelabs.com/test/guinea-pig");
		logger.info("title of page is: " + driver.getTitle());
	}

	@AfterMethod
	public void destroy() {
		if (driver != null) {
			driver.quit();
		}
	}

	private WebElement getElementByName(WebDriver driver, String name) {
		return getElementByName(driver, name, 10);
	}

	private WebElement getElementByName(WebDriver driver, String name, int timeout) {
		return (new WebDriverWait(driver, timeout)).until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
	}

	private WebElement getElementByXPath(WebDriver driver, String path) {
		return getElementByXPath(driver, path, 10);
	}

	private WebElement getElementByXPath(WebDriver driver, String path, int timeout) {
		return (new WebDriverWait(driver, timeout)).until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)));
	}
