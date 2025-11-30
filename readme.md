# 🚀 Test Automation Framework

A comprehensive, production-ready BDD/Cucumber test automation framework built with Spring Boot, featuring tag-based test execution, real-time monitoring, and advanced test management capabilities.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Usage Guide](#usage-guide)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## ✨ Features

### Core Features
- ✅ **BDD/Cucumber Integration** - Write tests in natural language
- ✅ **Tag-Based Execution** - Execute tests by tags with AND/OR logic
- ✅ **Feature Management** - Create, edit, and manage feature files via UI
- ✅ **Page Object Management** - Generate and manage page objects
- ✅ **Step Definition Generator** - Auto-generate step definitions
- ✅ **Test Data Management** - JSON-based test data handling
- ✅ **Real-Time Execution Monitoring** - WebSocket-based progress tracking
- ✅ **Execution History** - Track all test runs with detailed metrics
- ✅ **Report Management** - Cucumber HTML reports with download capability
- ✅ **REST API** - Complete REST API for automation

### Advanced Features
- 🎯 **Tag Management** - Create custom tags with types and colors
- 📊 **Statistics Dashboard** - Real-time framework statistics
- 🔄 **Async Test Execution** - Non-blocking test execution
- 📈 **Test Analytics** - Tag usage, success rates, trends
- 🎨 **Modern UI** - Responsive, mobile-friendly interface
- 🔐 **WebSocket Support** - Real-time updates
- 📦 **JSON Persistence** - Simple, file-based data storage
- 🚀 **Quick Actions** - One-click test execution

## 🛠 Technology Stack

### Backend
- **Spring Boot 2.7.14** - Application framework
- **Cucumber 7.14.0** - BDD testing framework
- **Selenium 4.12.1** - UI automation
- **REST Assured 5.3.1** - API testing
- **Jackson** - JSON processing
- **WebSocket** - Real-time communication

### Frontend
- **Thymeleaf** - Server-side templating
- **Bootstrap 5.1.3** - UI framework
- **Font Awesome 6.0** - Icons
- **SockJS & STOMP** - WebSocket client

### Testing
- **JUnit 5** - Test runner
- **AssertJ** - Fluent assertions
- **WebDriverManager** - Automatic driver management

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 11 or higher**
- **Maven 3.6 or higher**
- **Chrome Browser** (for UI tests)
- **Git** (for version control)

### Verify Installation

```bash
java -version
# Output: java version "11.0.x" or higher

mvn -version
# Output: Apache Maven 3.6.x or higher

git --version
# Output: git version 2.x.x
```

## 🚀 Installation

### Step 1: Clone the Repository

```bash
git clone <your-repository-url>
cd test-automation-framework
```

### Step 2: Build the Project

```bash
mvn clean install
```

### Step 3: Run the Application

```bash
mvn spring-boot:run
```

### Step 4: Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## 📁 Project Structure

```
test-automation-framework/
│
├── src/
│   ├── main/
│   │   ├── java/com/testautomation/
│   │   │   ├── TestAutomationFrameworkApplication.java
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── model/               # Data models
│   │   │   ├── service/             # Business logic
│   │   │   └── controller/          # REST & MVC controllers
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data/                # JSON data storage
│   │       ├── reports/             # Test reports
│   │       ├── templates/           # HTML templates
│   │       └── static/              # CSS, JS, images
│   │
│   └── test/
│       ├── java/com/testautomation/
│       │   ├── runners/             # Cucumber runners
│       │   └── stepdefinitions/     # Step definitions
│       │
│       └── resources/
│           ├── features/            # Feature files
│           ├── page-objects/        # Generated page objects
│           └── test-data/           # Test data files
│
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## 🎯 Quick Start

### 1. Create Your First Feature File

Navigate to **Features** page and create a new feature:

```gherkin
@smoke @api
Feature: User Login
  As a user
  I want to login to the application
  
  Scenario: Successful login
    Given the API endpoint is "https://api.example.com"
    When I send POST request to "/login"
    Then the response status code should be 200
```

### 2. Create Tags

Navigate to **Tag Management** and create tags:
- @smoke - Quick smoke tests
- @regression - Full regression suite
- @api - API tests
- @ui - UI tests

### 3. Execute Tests

Navigate to **Test Execution** and:
1. Select execution type (All/Tags/Feature)
2. Choose tags (for tag-based execution)
3. Select logic (AND/OR)
4. Preview matching features
5. Click "Execute Tests"

### 4. View Results

Check **Execution History** to see:
- Execution status (PASSED/FAILED)
- Duration and timestamps
- Scenario statistics
- Download reports

## 📖 Usage Guide

### Feature Management

**Create Feature:**
```gherkin
@smoke
Feature: Product Search
  Scenario: Search for product
    Given I am on the homepage
    When I search for "laptop"
    Then I should see search results
```

**Add Tags:**
1. Edit feature file
2. Add tags at the top: `@smoke @ui @regression`
3. Save changes

### Tag-Based Execution

**Execute with AND logic:**
```bash
mvn test -Dcucumber.filter.tags="@smoke and @api"
```

**Execute with OR logic:**
```bash
mvn test -Dcucumber.filter.tags="@smoke or @regression"
```

**Exclude tags:**
```bash
mvn test -Dcucumber.filter.tags="@regression and not @slow"
```

**Complex expressions:**
```bash
mvn test -Dcucumber.filter.tags="(@smoke or @regression) and not @wip"
```

### API Testing Example

```gherkin
@api @smoke
Feature: REST API Testing
  Scenario: Create user
    Given the API endpoint is "https://api.example.com"
    And I set Content-Type to "application/json"
    And I set request body field "name" to "John Doe"
    And I set request body field "email" to "john@example.com"
    When I send POST request to "/users"
    Then the response status code should be 201
    And the response should contain "id"
```

### UI Testing Example

```gherkin
@ui @smoke
Feature: Login Page
  Scenario: Successful login
    Given I navigate to "https://example.com/login"
    When I enter "username" into "usernameField"
    And I enter "password" into "passwordField"
    And I click "loginButton"
    Then I should see the text "Welcome"
```

## ⚙️ Configuration

### Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Browser Configuration
selenium.browser=chrome
selenium.headless=false

# API Configuration
api.base-url=https://api.example.com
api.timeout=30000

# Execution Configuration
test.execution.timeout=3600000
test.execution.parallel-threads=4
```

### Environment-Specific Configuration

Create environment-specific property files:
- `application-dev.properties`
- `application-qa.properties`
- `application-prod.properties`

Run with specific profile:
```bash
mvn spring-boot:run -Dspring.profiles.active=qa
```

## 📡 API Documentation

### REST Endpoints

#### Feature Files
- `GET /api/features` - Get all features
- `POST /api/features` - Create feature
- `PUT /api/features/{name}` - Update feature
- `DELETE /api/features/{name}` - Delete feature

#### Tags
- `GET /api/tags` - Get all tags
- `GET /api/tags/active` - Get active tags
- `POST /api/tags` - Create tag
- `PUT /api/tags/{name}` - Update tag
- `DELETE /api/tags/{name}` - Delete tag
- `POST /api/tags/execute` - Execute by tags

#### Test Execution
- `POST /api/execution/run/all` - Run all tests
- `POST /api/execution/run/feature/{name}` - Run specific feature
- `POST /api/tags/execute` - Run by tags
- `GET /api/execution/history` - Get execution history

#### Reports
- `GET /api/reports` - Get all reports
- `GET /api/reports/{id}` - Get specific report
- `DELETE /api/reports/{id}` - Delete report

### WebSocket Endpoints

Connect to: `ws://localhost:8080/ws`

Subscribe to: `/topic/execution-progress`

## 🎓 Best Practices

### Feature Files
1. Use descriptive feature and scenario names
2. Follow Given-When-Then format
3. Keep scenarios focused and independent
4. Use tags for categorization
5. Add Background for common setup steps

### Step Definitions
1. Keep steps atomic and reusable
2. Use regex patterns for flexibility
3. Add proper logging
4. Handle exceptions gracefully
5. Avoid hard-coded values

### Page Objects
1. One page object per page/component
2. Use meaningful element names
3. Prefer CSS selectors over XPath
4. Add wait strategies
5. Encapsulate page logic

### Test Data
1. Use environment-specific data
2. Keep test data separate from code
3. Use JSON for complex data structures
4. Implement data builders/factories
5. Clean up test data after execution

## 🐛 Troubleshooting

### Issue: Port 8080 already in use
**Solution:** Change port in `application.properties`:
```properties
server.port=8081
```

### Issue: ChromeDriver not found
**Solution:** WebDriverManager handles this automatically. If issues persist:
```bash
mvn clean install -U
```

### Issue: Tests not executing
**Solution:** Check:
1. Feature files exist in `src/test/resources/features`
2. Step definitions are in `com.testautomation.stepdefinitions` package
3. Tags are correctly specified
4. No syntax errors in feature files

### Issue: WebSocket connection failed
**Solution:** Ensure:
1. Application is running
2. Port 8080 is accessible
3. No firewall blocking WebSocket connections

### Issue: Reports not generating
**Solution:** Check:
1. Reports directory exists: `src/main/resources/reports/cucumber`
2. Write permissions are available
3. Cucumber plugin configuration in TestRunner

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Check existing documentation
- Review troubleshooting guide

## 📄 License

This project is licensed under the MIT License.

## 🎉 Acknowledgments

- Spring Boot Team
- Cucumber Team
- Selenium Team
- Open Source Community

---

**Made with ❤️ for Test Automation Excellence**

**Version:** 1.0.0  
**Last Updated:** 2024