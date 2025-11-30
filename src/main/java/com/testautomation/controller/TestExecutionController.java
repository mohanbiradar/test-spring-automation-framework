package com.testautomation.controller;

import com.testautomation.service.ExecutionHistoryService;
import com.testautomation.service.TestExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/execution")
public class TestExecutionController {
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionController.class);

    @Autowired
    private TestExecutionService executionService;
    @Autowired
    private ExecutionHistoryService historyService;

    @PostMapping("/run/all")
    public ResponseEntity<Map<String, Object>> runAllTests(
            @RequestParam(value = "executionId", required = false) String executionId) {
        Map<String, Object> response = new HashMap<>();
        if (!executionService.isMavenAvailable()) {
            response.put("success", false);
            response.put("message",
                    "Maven command not found. Configure 'test.execution.maven.command' or add a Maven wrapper.");
            return ResponseEntity.badRequest().body(response);
        }
        if (executionId == null || executionId.isBlank()) {
            executionId = executionService.createExecutionId();
        }
        executionService.runAllTestsWithId(executionId);
        logger.info("runAllTests started request with executionId={}", executionId);
        // send an initial notification so clients that establish websockets after call
        // get a start msg
        executionService.notifyExecutionStart(executionId);
        response.put("success", true);
        response.put("message", "Execution started");
        response.put("executionId", executionId);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/run/feature/{fileName}")
    public ResponseEntity<Map<String, Object>> runFeature(@PathVariable String fileName,
            @RequestParam(value = "executionId", required = false) String executionId) {
        Map<String, Object> response = new HashMap<>();
        if (!executionService.isMavenAvailable()) {
            response.put("success", false);
            response.put("message",
                    "Maven command not found. Configure 'test.execution.maven.command' or add a Maven wrapper.");
            return ResponseEntity.badRequest().body(response);
        }
        if (executionId == null || executionId.isBlank()) {
            executionId = executionService.createExecutionId();
        }
        executionService.runFeatureFileWithId(fileName, executionId);
        logger.info("runFeature started for {} with executionId={}", fileName, executionId);
        executionService.notifyExecutionStart(executionId);
        response.put("success", true);
        response.put("message", "Execution started");
        response.put("executionId", executionId);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/maven")
    public ResponseEntity<Map<String, Object>> mavenStatus() {
        Map<String, Object> response = new HashMap<>();
        boolean available = executionService.isMavenAvailable();
        response.put("available", available);
        response.put("command", executionService.getMavenCommandPath());
        response.put("message", available ? "Maven available"
                : "Maven not found. Configure 'test.execution.maven.command' or add a Maven wrapper (mvnw).");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> newExecutionId() {
        Map<String, Object> response = new HashMap<>();
        String executionId = executionService.createExecutionId();
        response.put("executionId", executionId);
        response.put("message", "New execution id");
        response.put("success", true);
        logger.info("Generated new executionId: {}", executionId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/{executionId}")
    public ResponseEntity<Map<String, Object>> cancelExecution(@PathVariable String executionId) {
        Map<String, Object> response = new HashMap<>();
        boolean cancelled = executionService.cancelExecution(executionId);
        response.put("executionId", executionId);
        response.put("cancelled", cancelled);
        response.put("success", cancelled);
        response.put("message", cancelled ? "Cancelled" : "Execution not found or already completed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{executionId}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable String executionId) {
        Map<String, Object> response = new HashMap<>();
        var exec = historyService.getExecutionById(executionId);
        if (exec == null) {
            response.put("success", false);
            response.put("message", "Execution id not found");
            return ResponseEntity.status(404).body(response);
        }
        response.put("success", true);
        response.put("execution", exec);
        return ResponseEntity.ok(response);
    }

}
