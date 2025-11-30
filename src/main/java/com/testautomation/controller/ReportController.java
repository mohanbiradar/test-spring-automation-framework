package com.testautomation.controller;

import com.testautomation.model.Report;
import com.testautomation.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Resource> getReport(@PathVariable String reportId) {
        File reportFile = reportService.getReportFile(reportId);
        Resource resource = new FileSystemResource(reportFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + reportFile.getName() + "\"")
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable String reportId) {
        File reportFile = reportService.getReportFile(reportId);
        Resource resource = new FileSystemResource(reportFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + reportFile.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable String reportId) {
        boolean deleted = reportService.deleteReport(reportId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted ? "Report deleted" : "Report not found");
        return ResponseEntity.ok(response);
    }
}
