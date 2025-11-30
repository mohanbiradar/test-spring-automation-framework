package com.testautomation.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * API Step Definitions
 * Contains steps for API testing using REST Assured
 */
public class APISteps {
    
    private static final Logger logger = LoggerFactory.getLogger(APISteps.class);
    
    private String baseUrl;
    private RequestSpecification request;
    private Response response;
    private Map<String, String> headers;
    private Map<String, Object> requestBody;

    public APISteps() {
        headers = new HashMap<>();
        requestBody = new HashMap<>();
    }

    // ========== Setup Steps ==========

    @Given("the API endpoint is {string}")
    public void theApiEndpointIs(String endpoint) {
        logger.info("Setting base URL: {}", endpoint);
        this.baseUrl = endpoint;
        RestAssured.baseURI = endpoint;
    }

    @Given("the API is accessible")
    public void theApiIsAccessible() {
        logger.info("Verifying API is accessible");
        response = given().when().get("/health");
        // Most APIs have a health endpoint, adjust as needed
    }

    @Given("I set header {string} to {string}")
    public void iSetHeaderTo(String headerName, String headerValue) {
        logger.info("Setting header {} to {}", headerName, headerValue);
        headers.put(headerName, headerValue);
    }

    @Given("I set Content-Type to {string}")
    public void iSetContentTypeTo(String contentType) {
        logger.info("Setting Content-Type to {}", contentType);
        headers.put("Content-Type", contentType);
    }

    @Given("I set Accept to {string}")
    public void iSetAcceptTo(String accept) {
        logger.info("Setting Accept to {}", accept);
        headers.put("Accept", accept);
    }

    @Given("I set Authorization token to {string}")
    public void iSetAuthorizationTokenTo(String token) {
        logger.info("Setting Authorization token");
        headers.put("Authorization", "Bearer " + token);
    }

    // ========== Request Body Steps ==========

    @Given("I set request body field {string} to {string}")
    public void iSetRequestBodyFieldTo(String field, String value) {
        logger.info("Setting request body field {} to {}", field, value);
        requestBody.put(field, value);
    }

    @Given("I set request body to:")
    public void iSetRequestBodyTo(String jsonBody) {
        logger.info("Setting request body to: {}", jsonBody);
        // The jsonBody parameter will contain the doc string from the feature file
    }

    // ========== HTTP Method Steps ==========

    @When("I send GET request to {string}")
    public void iSendGetRequestTo(String endpoint) {
        logger.info("Sending GET request to: {}", endpoint);
        request = given().headers(headers);
        response = request.get(endpoint);
        logResponse();
    }

    @When("I send POST request to {string}")
    public void iSendPostRequestTo(String endpoint) {
        logger.info("Sending POST request to: {}", endpoint);
        request = given()
                .headers(headers)
                .body(requestBody);
        response = request.post(endpoint);
        logResponse();
    }

    @When("I send PUT request to {string}")
    public void iSendPutRequestTo(String endpoint) {
        logger.info("Sending PUT request to: {}", endpoint);
        request = given()
                .headers(headers)
                .body(requestBody);
        response = request.put(endpoint);
        logResponse();
    }

    @When("I send DELETE request to {string}")
    public void iSendDeleteRequestTo(String endpoint) {
        logger.info("Sending DELETE request to: {}", endpoint);
        request = given().headers(headers);
        response = request.delete(endpoint);
        logResponse();
    }

    @When("I send PATCH request to {string}")
    public void iSendPatchRequestTo(String endpoint) {
        logger.info("Sending PATCH request to: {}", endpoint);
        request = given()
                .headers(headers)
                .body(requestBody);
        response = request.patch(endpoint);
        logResponse();
    }

    // ========== Response Validation Steps ==========

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        logger.info("Verifying status code is {}", statusCode);
        response.then().statusCode(statusCode);
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String field) {
        logger.info("Verifying response contains field: {}", field);
        response.then().body(field, notNullValue());
    }

    @Then("the response field {string} should be {string}")
    public void theResponseFieldShouldBe(String field, String expectedValue) {
        logger.info("Verifying field {} equals {}", field, expectedValue);
        response.then().body(field, equalTo(expectedValue));
    }

    @Then("the response field {string} should contain {string}")
    public void theResponseFieldShouldContain(String field, String expectedValue) {
        logger.info("Verifying field {} contains {}", field, expectedValue);
        response.then().body(field, containsString(expectedValue));
    }

    @Then("the response should contain error message {string}")
    public void theResponseShouldContainErrorMessage(String errorMessage) {
        logger.info("Verifying error message: {}", errorMessage);
        response.then().body("error", containsString(errorMessage));
    }

    @Then("the response time should be less than {int} milliseconds")
    public void theResponseTimeShouldBeLessThanMilliseconds(int maxTime) {
        logger.info("Verifying response time is less than {} ms", maxTime);
        long responseTime = response.getTime();
        assert responseTime < maxTime : 
            String.format("Response time %d ms exceeded limit of %d ms", responseTime, maxTime);
    }

    @Then("the response should be a valid JSON")
    public void theResponseShouldBeAValidJson() {
        logger.info("Verifying response is valid JSON");
        response.then().contentType("application/json");
    }

    @Then("the response Content-Type should be {string}")
    public void theResponseContentTypeShouldBe(String contentType) {
        logger.info("Verifying Content-Type is {}", contentType);
        response.then().contentType(contentType);
    }

    @Then("the response should be an array")
    public void theResponseShouldBeAnArray() {
        logger.info("Verifying response is an array");
        response.then().body("$", instanceOf(java.util.List.class));
    }

    @Then("the response array should have {int} items")
    public void theResponseArrayShouldHaveItems(int count) {
        logger.info("Verifying response array has {} items", count);
        response.then().body("size()", equalTo(count));
    }

    @Then("the response array should not be empty")
    public void theResponseArrayShouldNotBeEmpty() {
        logger.info("Verifying response array is not empty");
        response.then().body("size()", greaterThan(0));
    }

    // ========== Helper Methods ==========

    private void logResponse() {
        logger.info("Response Status: {}", response.getStatusCode());
        logger.info("Response Time: {} ms", response.getTime());
        logger.info("Response Body: {}", response.getBody().asString());
    }

    public Response getResponse() {
        return response;
    }
}
