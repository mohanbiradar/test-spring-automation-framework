package com.testautomation.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Common Step Definitions
 * Contains shared steps used across multiple features
 */
public class CommonSteps {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonSteps.class);
    private static WebDriver driver;

    /**
     * Setup method - runs before each scenario
     */
    @Before
    public void setup() {
        logger.info("Setting up test environment...");
        
        // Setup WebDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        // Uncomment for headless mode
        // options.addArguments("--headless");
        
        // Initialize WebDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        logger.info("WebDriver initialized successfully");
    }

    /**
     * Teardown method - runs after each scenario
     */
    @After
    public void teardown() {
        logger.info("Tearing down test environment...");
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver closed successfully");
        }
    }

    /**
     * Get WebDriver instance
     */
    public static WebDriver getDriver() {
        return driver;
    }

    // ========== Common Step Definitions ==========

    @Given("I wait for {int} seconds")
    public void iWaitForSeconds(int seconds) throws InterruptedException {
        logger.info("Waiting for {} seconds", seconds);
        Thread.sleep(seconds * 1000L);
    }

    @Given("I wait for {int} milliseconds")
    public void iWaitForMilliseconds(int milliseconds) throws InterruptedException {
        logger.info("Waiting for {} milliseconds", milliseconds);
        Thread.sleep(milliseconds);
    }

    @Given("the browser is open")
    public void theBrowserIsOpen() {
        logger.info("Verifying browser is open");
        assert driver != null : "Browser is not initialized";
    }

    @When("I refresh the page")
    public void iRefreshThePage() {
        logger.info("Refreshing the page");
        driver.navigate().refresh();
    }

    @When("I navigate back")
    public void iNavigateBack() {
        logger.info("Navigating back");
        driver.navigate().back();
    }

    @When("I navigate forward")
    public void iNavigateForward() {
        logger.info("Navigating forward");
        driver.navigate().forward();
    }

    @Then("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        logger.info("Verifying page title: {}", expectedTitle);
        String actualTitle = driver.getTitle();
        assert actualTitle.equals(expectedTitle) : 
            String.format("Expected title: %s, but got: %s", expectedTitle, actualTitle);
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String titlePart) {
        logger.info("Verifying page title contains: {}", titlePart);
        String actualTitle = driver.getTitle();
        assert actualTitle.contains(titlePart) : 
            String.format("Expected title to contain: %s, but got: %s", titlePart, actualTitle);
    }

    @Then("the current URL should be {string}")
    public void theCurrentUrlShouldBe(String expectedUrl) {
        logger.info("Verifying URL: {}", expectedUrl);
        String actualUrl = driver.getCurrentUrl();
        assert actualUrl.equals(expectedUrl) : 
            String.format("Expected URL: %s, but got: %s", expectedUrl, actualUrl);
    }

    @Then("the current URL should contain {string}")
    public void theCurrentUrlShouldContain(String urlPart) {
        logger.info("Verifying URL contains: {}", urlPart);
        String actualUrl = driver.getCurrentUrl();
        assert actualUrl.contains(urlPart) : 
            String.format("Expected URL to contain: %s, but got: %s", urlPart, actualUrl);
    }

    @Then("I close the browser")
    public void iCloseTheBrowser() {
        logger.info("Closing browser");
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Given("I am on the homepage")
    public void iAmOnTheHomepage() {
        logger.info("Navigating to homepage");
        // Configure your application URL
        driver.get("http://localhost:8080");
    }

    @Given("I navigate to {string}")
    public void iNavigateTo(String url) {
        logger.info("Navigating to: {}", url);
        driver.get(url);
    }

    @Then("I should see the text {string}")
    public void iShouldSeeTheText(String text) {
        logger.info("Verifying page contains text: {}", text);
        String pageSource = driver.getPageSource();
        assert pageSource.contains(text) : 
            String.format("Expected to find text: %s", text);
    }

    @Then("I should not see the text {string}")
    public void iShouldNotSeeTheText(String text) {
        logger.info("Verifying page does not contain text: {}", text);
        String pageSource = driver.getPageSource();
        assert !pageSource.contains(text) : 
            String.format("Did not expect to find text: %s", text);
    }
}
