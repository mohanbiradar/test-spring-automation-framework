package com.testautomation.service;

import com.testautomation.model.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final String REPORTS_DIR = "src/main/resources/reports/cucumber";

    public ReportService() {
        initializeReportsDirectory();
    }

    private void initializeReportsDirectory() {
        try {
            Path path = Paths.get(REPORTS_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Reports directory created: {}", REPORTS_DIR);
            }
        } catch (IOException e) {
            logger.error("Error creating reports directory", e);
        }
    }

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        File reportsDir = new File(REPORTS_DIR);

        if (reportsDir.exists() && reportsDir.isDirectory()) {
            File[] files = reportsDir.listFiles((dir, name) -> name.endsWith(".html") || name.endsWith(".json"));

            if (files != null) {
                for (File file : files) {
                    Report report = createReportFromFile(file);
                    reports.add(report);
                }
            }
        }

        return reports.stream()
                .sorted(Comparator.comparing(Report::getGeneratedDate).reversed())
                .collect(Collectors.toList());
    }

    public File getReportFile(String reportId) {
        File reportsDir = new File(REPORTS_DIR);
        File[] files = reportsDir.listFiles((dir, name) -> name.contains(reportId));

        if (files != null && files.length > 0) {
            return files[0];
        }

        // Log the lookup attempt and include the absolute path that was searched
        logger.warn("Report not found: {}. Tried path: {}", reportId, reportsDir.getAbsolutePath());
        throw new com.testautomation.exception.ReportNotFoundException("Report not found: " + reportId);
    }

    public boolean deleteReport(String reportId) {
        try {
            File report = getReportFile(reportId);
            boolean deleted = report.delete();
            if (deleted) {
                logger.info("Report deleted: {}", reportId);
            }
            return deleted;
        } catch (Exception e) {
            logger.error("Error deleting report: {}", reportId, e);
            return false;
        }
    }

    private Report createReportFromFile(File file) {
        Report report = new Report();
        report.setReportId(extractReportId(file.getName()));
        report.setReportName(file.getName());
        report.setReportPath(file.getAbsolutePath());
        report.setFileSize(file.length());
        report.setReportType(file.getName().endsWith(".html") ? "HTML" : "JSON");
        return report;
    }

    private String extractReportId(String fileName) {
        return fileName.replaceAll("cucumber-report-", "")
                .replaceAll("\\.html", "")
                .replaceAll("\\.json", "");
    }
}
