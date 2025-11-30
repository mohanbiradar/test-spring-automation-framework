package com.testautomation.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Report Model
 */
public class Report {
    private String reportId;
    private String reportName;
    private String reportType; // HTML, JSON, XML, PDF
    private String reportPath;
    private String executionId;
    private long fileSize;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedDate;
    
    private String status; // GENERATED, ARCHIVED, DELETED
    private int totalScenarios;
    private int passedScenarios;
    private int failedScenarios;
    private int skippedScenarios;
    private String duration;

    public Report() {
        this.reportId = "report_" + System.currentTimeMillis();
        this.generatedDate = LocalDateTime.now();
        this.status = "GENERATED";
        this.reportType = "HTML";
    }

    // Getters and Setters
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalScenarios() {
        return totalScenarios;
    }

    public void setTotalScenarios(int totalScenarios) {
        this.totalScenarios = totalScenarios;
    }

    public int getPassedScenarios() {
        return passedScenarios;
    }

    public void setPassedScenarios(int passedScenarios) {
        this.passedScenarios = passedScenarios;
    }

    public int getFailedScenarios() {
        return failedScenarios;
    }

    public void setFailedScenarios(int failedScenarios) {
        this.failedScenarios = failedScenarios;
    }

    public int getSkippedScenarios() {
        return skippedScenarios;
    }

    public void setSkippedScenarios(int skippedScenarios) {
        this.skippedScenarios = skippedScenarios;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Get human-readable file size
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.2f KB", fileSize / 1024.0);
        if (fileSize < 1024 * 1024 * 1024) return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Get pass percentage
     */
    public double getPassPercentage() {
        if (totalScenarios == 0) return 0.0;
        return (passedScenarios * 100.0) / totalScenarios;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reportName='" + reportName + '\'' +
                ", reportType='" + reportType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}