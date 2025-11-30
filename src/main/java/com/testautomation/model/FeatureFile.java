package com.testautomation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature File Model
 */
public class FeatureFile {
    private String fileName;
    private String featureName;
    private String description;
    private String content;
    private List<String> tags;
    private List<String> scenarios;
    private String status; // ACTIVE, INACTIVE, DRAFT
    private String author;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModified;
    
    private int scenarioCount;
    private int stepCount;

    public FeatureFile() {
        this.tags = new ArrayList<>();
        this.scenarios = new ArrayList<>();
        this.status = "ACTIVE";
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<String> scenarios) {
        this.scenarios = scenarios;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public int getScenarioCount() {
        return scenarioCount;
    }

    public void setScenarioCount(int scenarioCount) {
        this.scenarioCount = scenarioCount;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    @Override
    public String toString() {
        return "FeatureFile{" +
                "fileName='" + fileName + '\'' +
                ", featureName='" + featureName + '\'' +
                ", tags=" + tags +
                ", status='" + status + '\'' +
                '}';
    }
}
