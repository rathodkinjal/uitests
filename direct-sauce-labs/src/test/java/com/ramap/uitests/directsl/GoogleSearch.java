package com.ramap.uitests.directsl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GoogleSearch {

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
		Properties properties = new Properties();
		String homeDir = System.getProperty("user.home");
		properties.load(new FileInputStream(homeDir + "/uitests.properties"));
		String sauceUserName = properties.getProperty("sauceUserName");
		String sauceAccessKey = properties.getProperty("sauceAccessKey");
		String sauceUrl = "http://" + sauceUserName + ":" + sauceAccessKey + "@ondemand.saucelabs.com:80/wd/hub";

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setBrowserName(properties.getProperty("browserName"));
		caps.setPlatform(Platform.fromString(properties.getProperty("platform")));
		caps.setVersion(properties.getProperty("version"));

		driver = new RemoteWebDriver(new URL(sauceUrl), caps);
		logger.info("SauceLabs Session Id: " + driver.getSessionId());
		logger.info("SauceLabs Playback URL: https://saucelabs.com/beta/tests/" + driver.getSessionId());
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
}