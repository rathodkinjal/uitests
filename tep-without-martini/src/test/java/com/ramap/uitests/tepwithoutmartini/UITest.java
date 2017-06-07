package com.ramap.uitests.tepwithoutmartini;

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
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.internal.ApacheHttpClient;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class UITest {

	private RemoteWebDriver driver = null;
	private static Logger logger = Logger.getAnonymousLogger();;

	public static void main(String[] args) {
		System.err.println("Usage");
		System.err.println("Make sure you have a properties file name uitests.properties in your home directory");
		System.err.println("Sample Properties file....");
		System.err.println();
		System.err.println();
		System.err.println("tepUrl=https://testexecution.platform.intuit.net/v1");
		System.err.println("tepApiKey=prdakyxxxxxxxxxxxxxxxxxxxxxxxxxxx");
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
		System.out.println(properties);
		String tepUrl = properties.getProperty("tepUrl");
		String tepApiKey = properties.getProperty("tepApiKey");
		String authHeader = "Intuit_APIKey intuit_apikey="+ tepApiKey;

		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability("name", "Google-Test-Direct-TEP");
		caps.setBrowserName(properties.getProperty("browserName"));
		caps.setPlatform(Platform.fromString(properties.getProperty("platform")));
		caps.setVersion(properties.getProperty("version"));
		
		driver = createRemoteWebDriver(new URL(tepUrl), authHeader, caps);
		logger.info("TEP/Saucelabs Session Id: " + driver.getSessionId());
		logger.info("TEP/SauceLabs Playback URL: https://saucelabs.com/beta/tests/" + driver.getSessionId());
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

	@Test
	public void testDevInternal() {
		driver.get("https://devinternal.intuit.com/index.html");
		logger.info("title of page is: " + driver.getTitle());
		String searchQuery = "TEP";
		logger.info("Search for '" + searchQuery + "'");
		WebElement searchBox = getElementByXPath(driver, "//*[@id=\"headerSearchBox\"]");
		searchBox.sendKeys(searchQuery);
		WebElement searchButton = getElementByXPath(driver, "//*[@id=\"headerSearchBtn\"]");
		searchButton.click();
		// Wait for the results to come up
		WebElement results = getElementByXPath(driver, "//*[@id=\"keyword\"]/b");
		Assert.assertTrue(results.getText().contains(searchQuery));
		WebElement learn = getElementByXPath(driver, "//*[@id=\"TestExecution Platform_learn\"]");
		learn.click();
		//wait for page load
		WebElement mt = getElementByXPath(driver, "//*[@id=\"contentIframe\"]", 20);
		Assert.assertTrue(mt.isDisplayed());
		WebElement perfBtn = getElementByXPath(driver, "//*[@id=\"perfBtn\"]");
		Assert.assertEquals(perfBtn.getText(), "PERFORMANCE");
		perfBtn.click();
		WebElement perfPage = getElementByXPath(driver, "//*[@id=\"perfPage\"]");
		Assert.assertTrue(perfPage.isDisplayed());
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

	private RemoteWebDriver createRemoteWebDriver(URL url, String authHeader, DesiredCapabilities desiredCapabilities) {
		return createRemoteWebDriver(createCommandExecutor(url, authHeader), desiredCapabilities);
	}
	
	private RemoteWebDriver createRemoteWebDriver(CommandExecutor executor, DesiredCapabilities desiredCapabilities) {
		return new RemoteWebDriver(executor, desiredCapabilities);
	}

	private HttpCommandExecutor createCommandExecutor(URL url, String authHeader) {
		HttpClient.Factory httpClientFactory = _createHttpClientFactory(authHeader);
		HttpCommandExecutor executor = new HttpCommandExecutor(ImmutableMap.<String, CommandInfo>of(), url,
				httpClientFactory);
		return executor;
	}

	private static HttpClient.Factory _createHttpClientFactory(String authHeader) {
		return new ApacheHttpClient.Factory() {
			@Override
			public HttpClient createClient(URL url) {
				final HttpClient actualClient = super.createClient(url);
				HttpClient client = new HttpClient() {
					@Override
					public HttpResponse execute(HttpRequest request, boolean followRedirects) throws IOException {
						request.addHeader("Authorization", authHeader);
						return actualClient.execute(request, followRedirects);
					}

					@Override
					public void close() throws IOException {
						if (actualClient != null) {
							actualClient.close();
						}
					}
				};
				return client;
			}
		};
	}
}