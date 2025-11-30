package com.testautomation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Data Model
 */
public class TestData {
    private String dataSetName;
    private String category; // USER, PRODUCT, ORDER, CONFIGURATION, etc.
    private String environment; // DEV, QA, STAGING, PROD
    private Map<String, Object> data;
    private String description;
    private boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;

    public TestData() {
        this.data = new HashMap<>();
        this.active = true;
        this.environment = "QA";
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    public TestData(String dataSetName, String category) {
        this();
        this.dataSetName = dataSetName;
        this.category = category;
    }

    // Getters and Setters
    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Get data value by key
     */
    public Object getValue(String key) {
        return data.get(key);
    }

    /**
     * Set data value by key
     */
    public void setValue(String key, Object value) {
        this.data.put(key, value);
        this.lastModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TestData{" +
                "dataSetName='" + dataSetName + '\'' +
                ", category='" + category + '\'' +
                ", environment='" + environment + '\'' +
                ", active=" + active +
                '}';
    }
}
