package com.testautomation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object Model
 */
public class PageObject {
    private String pageName;
    private String packageName;
    private String description;
    private String pageUrl;
    private List<WebElement> elements;
    private List<String> methods;
    private String status; // ACTIVE, INACTIVE

    public PageObject() {
        this.elements = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.status = "ACTIVE";
    }

    /**
     * Web Element class for Page Object
     */
    public static class WebElement {
        private String elementName;
        private String locatorType; // ID, NAME, XPATH, CSS, CLASS_NAME, TAG_NAME, LINK_TEXT
        private String locatorValue;
        private String elementType; // BUTTON, TEXTBOX, DROPDOWN, CHECKBOX, RADIO, LINK, LABEL
        private String description;

        public WebElement() {
        }

        public WebElement(String elementName, String locatorType, String locatorValue, String elementType) {
            this.elementName = elementName;
            this.locatorType = locatorType;
            this.locatorValue = locatorValue;
            this.elementType = elementType;
        }

        // Getters and Setters
        public String getElementName() {
            return elementName;
        }

        public void setElementName(String elementName) {
            this.elementName = elementName;
        }

        public String getLocatorType() {
            return locatorType;
        }

        public void setLocatorType(String locatorType) {
            this.locatorType = locatorType;
        }

        public String getLocatorValue() {
            return locatorValue;
        }

        public void setLocatorValue(String locatorValue) {
            this.locatorValue = locatorValue;
        }

        public String getElementType() {
            return elementType;
        }

        public void setElementType(String elementType) {
            this.elementType = elementType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // Getters and Setters
    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<WebElement> getElements() {
        return elements;
    }

    public void setElements(List<WebElement> elements) {
        this.elements = elements;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PageObject{" +
                "pageName='" + pageName + '\'' +
                ", elements=" + elements.size() +
                ", status='" + status + '\'' +
                '}';
    }
}
