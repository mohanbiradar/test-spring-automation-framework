@smoke @api @regression
Feature: User Login API
  As a user
  I want to login to the application
  So that I can access my account

  Background:
    Given the API endpoint is "https://api.example.com"
    And the API is accessible

  @positive @critical
  Scenario: Successful login with valid credentials
    Given I have valid user credentials
      | username | password |
      | testuser | Test@123 |
    When I send POST request to "/api/login"
    Then the response status code should be 200
    And the response should contain "access_token"
    And the response should contain "refresh_token"

  @negative
  Scenario: Login fails with invalid password
    Given I have invalid credentials
      | username | password |
      | testuser | wrong123 |
    When I send POST request to "/api/login"
    Then the response status code should be 401
    And the response should contain error message "Invalid credentials"

  @negative @security
  Scenario: Login fails with SQL injection attempt
    Given I have malicious credentials
      | username              | password |
      | admin' OR '1'='1'--   | anything |
    When I send POST request to "/api/login"
    Then the response status code should be 400
    And the request should be rejected
