package com.ramap.uitests.localb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GoogleSearch {

	private WebDriver driver = null;
	private static Logger logger = Logger.getAnonymousLogger();;

	public static void main(String [] args) {
		System.err.println("Usage");
		System.err.println("Make sure you have a properties file name uitests.properties in your home directory");
		System.err.println("Make sure you have browser drivers installed");
		System.err.println("Chrome Driver: https://sites.google.com/a/chromium.org/chromedriver/downloads");
		System.err.println("Sample Properties file....");
		System.err.println();System.err.println();
		System.err.println("#Chrome Driver: https://sites.google.com/a/chromium.org/chromedriver/downloads");
		System.err.println("webdriver.chrome.driver=/path/to/your/chromedriver");
		System.err.println("#Gecko Driver for Firefox: https://github.com/mozilla/geckodriver/releases");
		System.err.println("#webdriver.firefox.marionette=/Applications/geckodriver");
		System.err.println("#webdriver.firefox.bin=/Applications/Firefox.app/Contents/MacOS/firefox-bin");
	}

	@BeforeMethod
	public void init() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		//String homeDir = System.getProperty("user.home");
		//properties.load(new FileInputStream(homeDir + "/uitests.properties"));
		
//		System.setProperty("webdriver.firefox.marionette", properties.getProperty("webdriver.firefox.marionette"));
//		System.setProperty("webdriver.firefox.bin", properties.getProperty("webdriver.firefox.bin"));
//		driver = new FirefoxDriver();

		//System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");		
		ChromeOptions options = new ChromeOptions();
		//options.setBinary("/usr/local/bin/chromedriver");

//		options.setBinary(properties.getProperty("webdriver.chrome.driver"));
//		options.addArguments("start-maximized");
		options.addArguments("headless");
		driver = new ChromeDriver(options);
		logger.info("WebDriver: " + driver.toString());
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
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
}
