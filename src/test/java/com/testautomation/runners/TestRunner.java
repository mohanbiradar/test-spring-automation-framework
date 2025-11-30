package com.testautomation.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Cucumber Test Runner
 *
 * This class is responsible for running Cucumber tests
 * Configure test execution options here
 */
@RunWith(Cucumber.class)
@CucumberOptions(
                // Location of feature files
                features = "src/test/resources/features",

                // Location of step definition classes
                glue = { "com.testautomation.stepdefinitions" },

                // Report plugins
                plugin = {
                                "pretty",
                                "html:target/cucumber-reports/cucumber-report.html",
                                "json:target/cucumber-reports/cucumber-report.json",
                                "junit:target/cucumber-reports/cucumber-report.xml"
                },

                // Note: 'strict' option removed in newer Cucumber versions; undefined steps
                // will fail by default using configuration

                // Whether to show step definition snippets for undefined steps
                snippets = CucumberOptions.SnippetType.CAMELCASE,

                // Tags to execute (can be overridden from command line)
                // Example: @smoke, @regression, "@smoke and @api"
                tags = "",

                // Whether to print stack traces in console
                monochrome = true,

                // Dry run mode - validates feature files without executing tests
                dryRun = false)
public class TestRunner {
        // This class remains empty - it's just a runner
        // All configuration is done via @CucumberOptions annotation
}

/**
 * Usage Examples:
 * 
 * 1. Run all tests:
 * mvn test
 * 
 * 2. Run tests with specific tag:
 * mvn test -Dcucumber.filter.tags="@smoke"
 * 
 * 3. Run tests with AND logic:
 * mvn test -Dcucumber.filter.tags="@smoke and @api"
 * 
 * 4. Run tests with OR logic:
 * mvn test -Dcucumber.filter.tags="@smoke or @regression"
 * 
 * 5. Run tests excluding certain tags:
 * mvn test -Dcucumber.filter.tags="not @wip"
 * 
 * 6. Complex tag expressions:
 * mvn test -Dcucumber.filter.tags="(@smoke or @regression) and not @slow"
 * 
 * 7. Run specific feature file:
 * mvn test -Dcucumber.features="src/test/resources/features/login.feature"
 * 
 * 8. Dry run to check feature files:
 * mvn test -Dcucumber.execution.dry-run=true
 */
