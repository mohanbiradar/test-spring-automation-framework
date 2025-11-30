package com.testautomation.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.List;

/**
 * UI Step Definitions
 * Contains steps for UI testing with Selenium
 */
public class UISteps {

    private static final Logger logger = LoggerFactory.getLogger(UISteps.class);
    private WebDriver driver;
    private WebDriverWait wait;

    public UISteps() {
        this.driver = CommonSteps.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ========== Element Interaction Steps ==========

    @When("I click {string}")
    public void iClick(String elementId) {
        logger.info("Clicking element: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
        element.click();
    }

    @When("I click on {string} by xpath")
    public void iClickByXpath(String xpath) {
        logger.info("Clicking element by xpath: {}", xpath);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();
    }

    @When("I click on {string} by css")
    public void iClickByCss(String cssSelector) {
        logger.info("Clicking element by CSS: {}", cssSelector);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
        element.click();
    }

    @When("I enter {string} into {string}")
    public void iEnterInto(String text, String elementId) {
        logger.info("Entering '{}' into element: {}", text, elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        element.clear();
        element.sendKeys(text);
    }

    @When("I enter {string} into {string} by xpath")
    public void iEnterIntoByXpath(String text, String xpath) {
        logger.info("Entering '{}' into element by xpath: {}", text, xpath);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        element.clear();
        element.sendKeys(text);
    }

    @When("I clear {string}")
    public void iClear(String elementId) {
        logger.info("Clearing element: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        element.clear();
    }

    // ========== Dropdown Steps ==========

    @When("I select {string} from dropdown {string}")
    public void iSelectFromDropdown(String optionText, String elementId) {
        logger.info("Selecting '{}' from dropdown: {}", optionText, elementId);
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        Select select = new Select(dropdown);
        select.selectByVisibleText(optionText);
    }

    @When("I select {string} from dropdown {string} by value")
    public void iSelectFromDropdownByValue(String value, String elementId) {
        logger.info("Selecting value '{}' from dropdown: {}", value, elementId);
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    // ========== Checkbox/Radio Steps ==========

    @When("I check the checkbox {string}")
    public void iCheckTheCheckbox(String elementId) {
        logger.info("Checking checkbox: {}", elementId);
        WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    @When("I uncheck the checkbox {string}")
    public void iUncheckTheCheckbox(String elementId) {
        logger.info("Unchecking checkbox: {}", elementId);
        WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        if (checkbox.isSelected()) {
            checkbox.click();
        }
    }

    @When("I select radio button {string}")
    public void iSelectRadioButton(String elementId) {
        logger.info("Selecting radio button: {}", elementId);
        WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
        if (!radio.isSelected()) {
            radio.click();
        }
    }

    // ========== Visibility/Existence Steps ==========

    @Then("I should see {string}")
    public void iShouldSee(String elementId) {
        logger.info("Verifying element is visible: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        assert element.isDisplayed() : "Element is not visible: " + elementId;
    }

    @Then("I should not see {string}")
    public void iShouldNotSee(String elementId) {
        logger.info("Verifying element is not visible: {}", elementId);
        boolean isInvisible = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(elementId)));
        assert isInvisible : "Element is still visible: " + elementId;
    }

    @Then("element {string} should be enabled")
    public void elementShouldBeEnabled(String elementId) {
        logger.info("Verifying element is enabled: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        assert element.isEnabled() : "Element is not enabled: " + elementId;
    }

    @Then("element {string} should be disabled")
    public void elementShouldBeDisabled(String elementId) {
        logger.info("Verifying element is disabled: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        assert !element.isEnabled() : "Element is not disabled: " + elementId;
    }

    // ========== Text Verification Steps ==========

    @Then("element {string} should contain text {string}")
    public void elementShouldContainText(String elementId, String expectedText) {
        logger.info("Verifying element {} contains text: {}", elementId, expectedText);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        String actualText = element.getText();
        assert actualText.contains(expectedText)
                : String.format("Expected text '%s' not found. Actual: '%s'", expectedText, actualText);
    }

    @Then("element {string} should have text {string}")
    public void elementShouldHaveText(String elementId, String expectedText) {
        logger.info("Verifying element {} has text: {}", elementId, expectedText);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        String actualText = element.getText();
        assert actualText.equals(expectedText)
                : String.format("Expected: '%s', Actual: '%s'", expectedText, actualText);
    }

    @Then("element {string} should have value {string}")
    public void elementShouldHaveValue(String elementId, String expectedValue) {
        logger.info("Verifying element {} has value: {}", elementId, expectedValue);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        String actualValue = element.getAttribute("value");
        assert actualValue.equals(expectedValue)
                : String.format("Expected value: '%s', Actual: '%s'", expectedValue, actualValue);
    }

    // ========== Wait Steps ==========

    @Given("I wait for element {string} to be visible")
    public void iWaitForElementToBeVisible(String elementId) {
        logger.info("Waiting for element to be visible: {}", elementId);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
    }

    @Given("I wait for element {string} to be clickable")
    public void iWaitForElementToBeClickable(String elementId) {
        logger.info("Waiting for element to be clickable: {}", elementId);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
    }

    @Given("I wait for element {string} to disappear")
    public void iWaitForElementToDisappear(String elementId) {
        logger.info("Waiting for element to disappear: {}", elementId);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(elementId)));
    }

    // ========== Mouse Actions ==========

    @When("I hover over {string}")
    public void iHoverOver(String elementId) {
        logger.info("Hovering over element: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(element).perform();
    }

    @When("I double click {string}")
    public void iDoubleClick(String elementId) {
        logger.info("Double clicking element: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.doubleClick(element).perform();
    }

    @When("I right click {string}")
    public void iRightClick(String elementId) {
        logger.info("Right clicking element: {}", elementId);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.contextClick(element).perform();
    }

    // ========== Window/Frame Steps ==========

    @When("I switch to frame {string}")
    public void iSwitchToFrame(String frameId) {
        logger.info("Switching to frame: {}", frameId);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameId));
    }

    @When("I switch to default content")
    public void iSwitchToDefaultContent() {
        logger.info("Switching to default content");
        driver.switchTo().defaultContent();
    }

    @When("I switch to window {int}")
    public void iSwitchToWindow(int windowIndex) {
        logger.info("Switching to window: {}", windowIndex);
        List<String> windows = driver.getWindowHandles().stream().toList();
        driver.switchTo().window(windows.get(windowIndex));
    }

    // ========== Alert Steps ==========

    @Then("I should see an alert")
    public void iShouldSeeAnAlert() {
        logger.info("Verifying alert is present");
        wait.until(ExpectedConditions.alertIsPresent());
    }

    @When("I accept the alert")
    public void iAcceptTheAlert() {
        logger.info("Accepting alert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    @When("I dismiss the alert")
    public void iDismissTheAlert() {
        logger.info("Dismissing alert");
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }

    @Then("alert text should be {string}")
    public void alertTextShouldBe(String expectedText) {
        logger.info("Verifying alert text: {}", expectedText);
        wait.until(ExpectedConditions.alertIsPresent());
        String actualText = driver.switchTo().alert().getText();
        assert actualText.equals(expectedText)
                : String.format("Expected alert text: '%s', Actual: '%s'", expectedText, actualText);
    }

    // ========== Screenshot Step ==========

    @When("I take a screenshot")
    public void iTakeAScreenshot() {
        logger.info("Taking screenshot");
        try {
            File screenshot = ((org.openqa.selenium.TakesScreenshot) driver)
                    .getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
            java.nio.file.Files.copy(screenshot.toPath(),
                    new File("target/screenshots/" + fileName).toPath());
            logger.info("Screenshot saved: {}", fileName);
        } catch (Exception e) {
            logger.error("Error taking screenshot", e);
        }
    }
}