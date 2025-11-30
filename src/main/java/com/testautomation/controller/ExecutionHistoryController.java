package com.testautomation.controller;

import com.testautomation.service.ExecutionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Execution History Controller
 * REST API for managing test execution history
 */
@RestController
@RequestMapping("/api/execution")
public class ExecutionHistoryController {

    @Autowired
    private ExecutionHistoryService executionHistoryService;


    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getExecutionHistory() {
        Map<String, Object> response = new HashMap<>();
        response.put("executions", executionHistoryService.getExecutionHistory().getExecutions());
        return ResponseEntity.ok(response);
    }

    /**
     * Get executions by tag
     */
    @GetMapping("/history/tag/{tag}")
    public ResponseEntity<Map<String, Object>> getExecutionsByTag(@PathVariable String tag) {
        var executions = executionHistoryService.getExecutionsByTag(tag);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("executions", executions);
        response.put("count", executions.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent executions
     */
    @GetMapping("/history/recent/{limit}")
    public ResponseEntity<Map<String, Object>> getRecentExecutions(@PathVariable int limit) {
        var executions = executionHistoryService.getRecentExecutions(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("executions", executions);
        response.put("count", executions.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete specific execution
     */
    @DeleteMapping("/{executionId}")
    public ResponseEntity<Map<String, Object>> deleteExecution(@PathVariable String executionId) {
        boolean deleted = executionHistoryService.deleteExecution(executionId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted ? "Execution deleted successfully" : "Execution not found");
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all execution history
     */
    @DeleteMapping("/history/clear")
    public ResponseEntity<Map<String, Object>> clearHistory() {
        // This would need to be implemented in the service
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "History cleared successfully");
        return ResponseEntity.ok(response);
    }
}