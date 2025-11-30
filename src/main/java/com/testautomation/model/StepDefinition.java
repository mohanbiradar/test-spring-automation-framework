package com.testautomation.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Step Definition Model
 */
public class StepDefinition {
    private String stepId;
    private String stepPattern;
    private String stepType; // GIVEN, WHEN, THEN, AND, BUT
    private String methodName;
    private String className;
    private List<String> parameters;
    private String description;
    private String implementation;
    private boolean isGenerated;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private String author;
    private int usageCount;

    public StepDefinition() {
        this.stepId = "step_" + System.currentTimeMillis();
        this.parameters = new ArrayList<>();
        this.isGenerated = false;
        this.createdDate = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.usageCount = 0;
    }

    // Getters and Setters
    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepPattern() {
        return stepPattern;
    }

    public void setStepPattern(String stepPattern) {
        this.stepPattern = stepPattern;
    }

    public String getStepType() {
        return stepType;
    }

    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    /**
     * Generate method signature from step pattern
     */
    public String generateMethodSignature() {
        StringBuilder signature = new StringBuilder();
        signature.append("@").append(stepType).append("(\"").append(stepPattern).append("\")\n");
        signature.append("public void ").append(methodName).append("(");
        
        if (parameters != null && !parameters.isEmpty()) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) signature.append(", ");
                signature.append("String param").append(i + 1);
            }
        }
        
        signature.append(") {\n");
        signature.append("    // TODO: Implement step definition\n");
        signature.append("}");
        
        return signature.toString();
    }

    @Override
    public String toString() {
        return "StepDefinition{" +
                "stepPattern='" + stepPattern + '\'' +
                ", stepType='" + stepType + '\'' +
                ", methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}