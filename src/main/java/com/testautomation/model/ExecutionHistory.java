package com.testautomation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Execution History with Tag Support
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionHistory {
    private List<Execution> executions;

    public ExecutionHistory() {
        this.executions = new ArrayList<>();
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    /**
     * Enhanced Execution class with tag support
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Execution {
        private String executionId;
        private String executionType; // ALL, FEATURE, TAG_BASED, COMPLEX_TAG, SUITE
        private String timestamp;
        private String status; // RUNNING, PASSED, FAILED
        private String duration;
        private String reportPath;
        
        // Feature-based execution
        private List<String> featureFiles;
        
        // Tag-based execution
        private List<String> tags;
        private String tagLogic; // AND, OR
        private List<String> excludeTags;
        
        // Execution statistics
        private Integer totalScenarios;
        private Integer passedScenarios;
        private Integer failedScenarios;
        private Integer skippedScenarios;
        private Integer totalSteps;
        private Integer passedSteps;
        private Integer failedSteps;
        private Integer skippedSteps;
        private Integer pendingSteps;
        
        // Additional metadata
        private String environment;
        private String browser;
        private String triggeredBy;
        private String notes;

        public Execution() {
            this.executionId = "exec_" + System.currentTimeMillis();
            this.timestamp = LocalDateTime.now().toString();
            this.status = "RUNNING";
            this.featureFiles = new ArrayList<>();
            this.tags = new ArrayList<>();
            this.excludeTags = new ArrayList<>();
        }

        // Getters and Setters
        public String getExecutionId() {
            return executionId;
        }

        public void setExecutionId(String executionId) {
            this.executionId = executionId;
        }

        public String getExecutionType() {
            return executionType;
        }

        public void setExecutionType(String executionType) {
            this.executionType = executionType;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getReportPath() {
            return reportPath;
        }

        public void setReportPath(String reportPath) {
            this.reportPath = reportPath;
        }

        public List<String> getFeatureFiles() {
            return featureFiles;
        }

        public void setFeatureFiles(List<String> featureFiles) {
            this.featureFiles = featureFiles;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public String getTagLogic() {
            return tagLogic;
        }

        public void setTagLogic(String tagLogic) {
            this.tagLogic = tagLogic;
        }

        public List<String> getExcludeTags() {
            return excludeTags;
        }

        public void setExcludeTags(List<String> excludeTags) {
            this.excludeTags = excludeTags;
        }

        public Integer getTotalScenarios() {
            return totalScenarios != null ? totalScenarios : 0;
        }

        public void setTotalScenarios(Integer totalScenarios) {
            this.totalScenarios = totalScenarios;
        }

        public Integer getPassedScenarios() {
            return passedScenarios != null ? passedScenarios : 0;
        }

        public void setPassedScenarios(Integer passedScenarios) {
            this.passedScenarios = passedScenarios;
        }

        public Integer getFailedScenarios() {
            return failedScenarios != null ? failedScenarios : 0;
        }

        public void setFailedScenarios(Integer failedScenarios) {
            this.failedScenarios = failedScenarios;
        }

        public Integer getSkippedScenarios() {
            return skippedScenarios != null ? skippedScenarios : 0;
        }

        public void setSkippedScenarios(Integer skippedScenarios) {
            this.skippedScenarios = skippedScenarios;
        }

        public Integer getTotalSteps() {
            return totalSteps != null ? totalSteps : 0;
        }

        public void setTotalSteps(Integer totalSteps) {
            this.totalSteps = totalSteps;
        }

        public Integer getPassedSteps() {
            return passedSteps != null ? passedSteps : 0;
        }

        public void setPassedSteps(Integer passedSteps) {
            this.passedSteps = passedSteps;
        }

        public Integer getFailedSteps() {
            return failedSteps != null ? failedSteps : 0;
        }

        public void setFailedSteps(Integer failedSteps) {
            this.failedSteps = failedSteps;
        }

        public Integer getSkippedSteps() {
            return skippedSteps != null ? skippedSteps : 0;
        }

        public void setSkippedSteps(Integer skippedSteps) {
            this.skippedSteps = skippedSteps;
        }

        public Integer getPendingSteps() {
            return pendingSteps != null ? pendingSteps : 0;
        }

        public void setPendingSteps(Integer pendingSteps) {
            this.pendingSteps = pendingSteps;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String getBrowser() {
            return browser;
        }

        public void setBrowser(String browser) {
            this.browser = browser;
        }

        public String getTriggeredBy() {
            return triggeredBy;
        }

        public void setTriggeredBy(String triggeredBy) {
            this.triggeredBy = triggeredBy;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        /**
         * Calculate pass percentage
         */
        public double getPassPercentage() {
            if (totalScenarios == null || totalScenarios == 0) {
                return 0.0;
            }
            return (passedScenarios != null ? passedScenarios : 0) * 100.0 / totalScenarios;
        }

        /**
         * Get execution summary
         */
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("Type: ").append(executionType != null ? executionType : "N/A");
            
            if (tags != null && !tags.isEmpty()) {
                summary.append(" | Tags: ").append(String.join(", ", tags));
                if (tagLogic != null) {
                    summary.append(" (").append(tagLogic).append(")");
                }
            }
            
            if (excludeTags != null && !excludeTags.isEmpty()) {
                summary.append(" | Excluding: ").append(String.join(", ", excludeTags));
            }
            
            summary.append(" | Scenarios: ").append(getTotalScenarios());
            summary.append(" (✓ ").append(getPassedScenarios());
            summary.append(" | ✗ ").append(getFailedScenarios()).append(")");
            
            return summary.toString();
        }

        @Override
        public String toString() {
            return "Execution{" +
                    "executionId='" + executionId + '\'' +
                    ", executionType='" + executionType + '\'' +
                    ", status='" + status + '\'' +
                    ", tags=" + tags +
                    ", totalScenarios=" + totalScenarios +
                    ", passedScenarios=" + passedScenarios +
                    ", failedScenarios=" + failedScenarios +
                    '}';
        }
    }
}