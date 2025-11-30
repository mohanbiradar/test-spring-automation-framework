package com.testautomation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.model.PageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PageObjectService {
    private static final Logger logger = LoggerFactory.getLogger(PageObjectService.class);
    private static final String PAGE_OBJECTS_FILE = "src/main/resources/data/page-objects.json";
    private static final String PAGE_OBJECTS_DIR = "src/test/resources/page-objects";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PageObjectService() {
        initializePageObjectsFile();
        initializePageObjectsDirectory();
    }

    private void initializePageObjectsFile() {
        try {
            File file = new File(PAGE_OBJECTS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                objectMapper.writeValue(file, new ArrayList<PageObject>());
                logger.info("Page objects file initialized: {}", PAGE_OBJECTS_FILE);
            }
        } catch (IOException e) {
            logger.error("Error initializing page objects file", e);
        }
    }

    private void initializePageObjectsDirectory() {
        try {
            Path path = Paths.get(PAGE_OBJECTS_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Page objects directory created: {}", PAGE_OBJECTS_DIR);
            }
        } catch (IOException e) {
            logger.error("Error creating page objects directory", e);
        }
    }

    /**
     * Get all page objects
     */
    public List<PageObject> getAllPageObjects() {
        try {
            File file = new File(PAGE_OBJECTS_FILE);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file, new TypeReference<List<PageObject>>() {});
        } catch (IOException e) {
            logger.error("Error reading page objects", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get page object by name
     */
    public Optional<PageObject> getPageObjectByName(String pageName) {
        return getAllPageObjects().stream()
                .filter(po -> po.getPageName().equalsIgnoreCase(pageName))
                .findFirst();
    }

    /**
     * Create page object
     */
    public PageObject createPageObject(PageObject pageObject) {
        List<PageObject> pageObjects = getAllPageObjects();
        
        if (pageObjects.stream().anyMatch(po -> po.getPageName().equals(pageObject.getPageName()))) {
            throw new IllegalArgumentException("Page object already exists: " + pageObject.getPageName());
        }

        pageObjects.add(pageObject);
        savePageObjects(pageObjects);
        logger.info("Page object created: {}", pageObject.getPageName());
        
        return pageObject;
    }

    /**
     * Update page object
     */
    public PageObject updatePageObject(String pageName, PageObject updatedPageObject) {
        List<PageObject> pageObjects = getAllPageObjects();
        
        Optional<PageObject> existing = pageObjects.stream()
                .filter(po -> po.getPageName().equalsIgnoreCase(pageName))
                .findFirst();

        if (!existing.isPresent()) {
            throw new IllegalArgumentException("Page object not found: " + pageName);
        }

        pageObjects.remove(existing.get());
        pageObjects.add(updatedPageObject);
        savePageObjects(pageObjects);
        logger.info("Page object updated: {}", updatedPageObject.getPageName());
        
        return updatedPageObject;
    }

    /**
     * Delete page object
     */
    public boolean deletePageObject(String pageName) {
        List<PageObject> pageObjects = getAllPageObjects();
        boolean removed = pageObjects.removeIf(po -> po.getPageName().equalsIgnoreCase(pageName));
        
        if (removed) {
            savePageObjects(pageObjects);
            
            // Delete generated Java file
            String javaFileName = pageName + ".java";
            File javaFile = new File(PAGE_OBJECTS_DIR, javaFileName);
            if (javaFile.exists()) {
                javaFile.delete();
                logger.info("Deleted Java file: {}", javaFileName);
            }
            
            logger.info("Page object deleted: {}", pageName);
        }
        
        return removed;
    }

    /**
     * Generate page object Java class
     */
    public String generatePageObjectClass(PageObject pageObject) {
        StringBuilder classCode = new StringBuilder();
        String className = pageObject.getPageName();
        String packageName = pageObject.getPackageName() != null ? 
                            pageObject.getPackageName() : "com.testautomation.pages";

        // Package declaration
        classCode.append("package ").append(packageName).append(";\n\n");

        // Imports
        classCode.append("import org.openqa.selenium.By;\n");
        classCode.append("import org.openqa.selenium.WebDriver;\n");
        classCode.append("import org.openqa.selenium.WebElement;\n");
        classCode.append("import org.openqa.selenium.support.ui.ExpectedConditions;\n");
        classCode.append("import org.openqa.selenium.support.ui.WebDriverWait;\n");
        classCode.append("import java.time.Duration;\n\n");

        // Class declaration
        classCode.append("/**\n");
        classCode.append(" * Page Object: ").append(className).append("\n");
        if (pageObject.getDescription() != null) {
            classCode.append(" * ").append(pageObject.getDescription()).append("\n");
        }
        classCode.append(" * Auto-generated by Test Automation Framework\n");
        classCode.append(" */\n");
        classCode.append("public class ").append(className).append(" {\n\n");

        // Fields
        classCode.append("    private WebDriver driver;\n");
        classCode.append("    private WebDriverWait wait;\n\n");

        // Page URL
        if (pageObject.getPageUrl() != null && !pageObject.getPageUrl().isEmpty()) {
            classCode.append("    private static final String PAGE_URL = \"")
                     .append(pageObject.getPageUrl()).append("\";\n\n");
        }

        // Element locators
        for (PageObject.WebElement element : pageObject.getElements()) {
            String locatorType = element.getLocatorType();
            String locatorValue = element.getLocatorValue();
            String elementName = element.getElementName().toUpperCase();
            
            classCode.append("    private static final By ").append(elementName)
                     .append(" = By.").append(getLocatorMethod(locatorType))
                     .append("(\"").append(locatorValue).append("\");\n");
        }
        classCode.append("\n");

        // Constructor
        classCode.append("    public ").append(className).append("(WebDriver driver) {\n");
        classCode.append("        this.driver = driver;\n");
        classCode.append("        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));\n");
        classCode.append("    }\n\n");

        // Navigate to page method
        if (pageObject.getPageUrl() != null && !pageObject.getPageUrl().isEmpty()) {
            classCode.append("    public void navigateToPage() {\n");
            classCode.append("        driver.get(PAGE_URL);\n");
            classCode.append("    }\n\n");
        }

        // Element interaction methods
        for (PageObject.WebElement element : pageObject.getElements()) {
            String elementName = element.getElementName();
            String methodName = toCamelCase(elementName);
            String elementType = element.getElementType();
            String locatorConst = elementName.toUpperCase();

            // Click method for buttons, links
            if ("BUTTON".equals(elementType) || "LINK".equals(elementType)) {
                classCode.append("    public void click").append(capitalize(methodName)).append("() {\n");
                classCode.append("        wait.until(ExpectedConditions.elementToBeClickable(")
                         .append(locatorConst).append(")).click();\n");
                classCode.append("    }\n\n");
            }

            // Enter text method for textboxes
            if ("TEXTBOX".equals(elementType)) {
                classCode.append("    public void enter").append(capitalize(methodName))
                         .append("(String text) {\n");
                classCode.append("        wait.until(ExpectedConditions.visibilityOfElementLocated(")
                         .append(locatorConst).append(")).sendKeys(text);\n");
                classCode.append("    }\n\n");
            }

            // Get text method for labels
            if ("LABEL".equals(elementType)) {
                classCode.append("    public String get").append(capitalize(methodName))
                         .append("Text() {\n");
                classCode.append("        return wait.until(ExpectedConditions.visibilityOfElementLocated(")
                         .append(locatorConst).append(")).getText();\n");
                classCode.append("    }\n\n");
            }

            // Generic getter
            classCode.append("    public WebElement get").append(capitalize(methodName))
                     .append("() {\n");
            classCode.append("        return wait.until(ExpectedConditions.presenceOfElementLocated(")
                     .append(locatorConst).append("));\n");
            classCode.append("    }\n\n");
        }

        classCode.append("}\n");

        return classCode.toString();
    }

    /**
     * Save page object class to file
     */
    public void savePageObjectClass(PageObject pageObject) {
        try {
            String classCode = generatePageObjectClass(pageObject);
            String fileName = pageObject.getPageName() + ".java";
            Path filePath = Paths.get(PAGE_OBJECTS_DIR, fileName);
            
            Files.write(filePath, classCode.getBytes());
            logger.info("Page object class saved: {}", fileName);
        } catch (IOException e) {
            logger.error("Error saving page object class", e);
            throw new RuntimeException("Failed to save page object class", e);
        }
    }

    private void savePageObjects(List<PageObject> pageObjects) {
        try {
            File file = new File(PAGE_OBJECTS_FILE);
            file.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, pageObjects);
        } catch (IOException e) {
            logger.error("Error saving page objects", e);
            throw new RuntimeException("Failed to save page objects", e);
        }
    }

    private String getLocatorMethod(String locatorType) {
        switch (locatorType.toUpperCase()) {
            case "ID": return "id";
            case "NAME": return "name";
            case "XPATH": return "xpath";
            case "CSS": return "cssSelector";
            case "CLASS_NAME": return "className";
            case "TAG_NAME": return "tagName";
            case "LINK_TEXT": return "linkText";
            case "PARTIAL_LINK_TEXT": return "partialLinkText";
            default: return "cssSelector";
        }
    }

    private String toCamelCase(String text) {
        String[] words = text.split("_");
        StringBuilder result = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            result.append(capitalize(words[i].toLowerCase()));
        }
        return result.toString();
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}