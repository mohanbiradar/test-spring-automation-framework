package com.testautomation;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Main Spring Boot Application for Test Automation Framework
 * 
 * Features:
 * - BDD/Cucumber test automation
 * - Tag-based test execution
 * - Feature file management
 * - Page object management
 * - Step definition generation
 * - Test data management
 * - Real-time execution monitoring
 * - Report generation and management
 */
@SpringBootApplication
@EnableAsync
public class TestAutomationFrameworkApplication {

    private static final Logger logger = LoggerFactory.getLogger(TestAutomationFrameworkApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TestAutomationFrameworkApplication.class, args);
        logger.info("╔══════════════════════════════════════════════════════════════╗");
        logger.info("║     Test Automation Framework Started Successfully! 🚀      ║");
        logger.info("║                                                              ║");
        logger.info("║  Access the application at: http://localhost:8080           ║");
        logger.info("║                                                              ║");
        logger.info("║  Features Available:                                         ║");
        logger.info("║  ✓ Feature File Management                                   ║");
        logger.info("║  ✓ Tag-Based Test Execution                                  ║");
        logger.info("║  ✓ Page Object Management                                    ║");
        logger.info("║  ✓ Step Definition Generation                                ║");
        logger.info("║  ✓ Test Data Management                                      ║");
        logger.info("║  ✓ Execution History & Reports                               ║");
        logger.info("╚══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Initialize required directories on application startup
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing Test Automation Framework...");

        createDirectoryIfNotExists("src/main/resources/data");
        createDirectoryIfNotExists("src/main/resources/reports");
        createDirectoryIfNotExists("src/main/resources/reports/cucumber");
        createDirectoryIfNotExists("src/test/resources/features");
        createDirectoryIfNotExists("src/test/resources/features/api");
        createDirectoryIfNotExists("src/test/resources/features/ui");
        createDirectoryIfNotExists("src/test/resources/page-objects");
        createDirectoryIfNotExists("src/test/resources/test-data");

        logger.info("Framework initialization completed successfully!");
    }

    /**
     * Create directory if it doesn't exist
     */
    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Created directory: {}", path);
            } else {
                logger.warn("Failed to create directory: {}", path);
            }
        }
    }

    /**
     * Configure CORS for REST API calls
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Use allowedOriginPatterns to support wildcard origins when credentials are
                        // allowed
                        .allowedOriginPatterns("*")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
}
