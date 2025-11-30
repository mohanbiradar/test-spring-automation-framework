package com.testautomation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home Controller - Main navigation controller
 */
@Controller
public class HomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model) {
        logger.info("Accessing home page");
        model.addAttribute("pageTitle", "Test Automation Framework");
        return "index";
    }

    /**
     * Feature management page
     */
    @GetMapping("/features")
    public String features(Model model) {
        logger.info("Accessing features page");
        model.addAttribute("pageTitle", "Feature Management");
        return "features";
    }

    /**
     * Page object management page
     */
    @GetMapping("/page-objects")
    public String pageObjects(Model model) {
        logger.info("Accessing page objects page");
        model.addAttribute("pageTitle", "Page Object Management");
        return "page-objects";
    }

    /**
     * Step definitions page
     */
    @GetMapping("/step-definitions")
    public String stepDefinitions(Model model) {
        logger.info("Accessing step definitions page");
        model.addAttribute("pageTitle", "Step Definitions");
        return "step-definitions";
    }

    /**
     * Test data management page
     */
    @GetMapping("/test-data")
    public String testData(Model model) {
        logger.info("Accessing test data page");
        model.addAttribute("pageTitle", "Test Data Management");
        return "test-data";
    }

    /**
     * Test execution page
     */
    @GetMapping("/execution")
    public String execution(Model model) {
        logger.info("Accessing test execution page");
        model.addAttribute("pageTitle", "Test Execution");
        return "test-execution";
    }

    /**
     * Execution history page
     */
    @GetMapping("/execution/history")
    public String executionHistory(Model model) {
        logger.info("Accessing execution history page");
        model.addAttribute("pageTitle", "Execution History");
        return "execution-history";
    }

    /**
     * Reports page
     */
    @GetMapping("/reports")
    public String reports(Model model) {
        logger.info("Accessing reports page");
        model.addAttribute("pageTitle", "Test Reports");
        return "reports";
    }

    /**
     * Tag management page
     */
    @GetMapping("/tags/management")
    public String tagManagement(Model model) {
        logger.info("Accessing tag management page");
        model.addAttribute("pageTitle", "Tag Management");
        return "tag-management";
    }
}
