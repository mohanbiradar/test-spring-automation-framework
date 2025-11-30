package com.testautomation.controller;

import com.testautomation.model.StepDefinition;
import com.testautomation.service.StepDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/step-definitions")
public class StepDefinitionController {

    @Autowired
    private StepDefinitionService stepDefinitionService;

    @GetMapping
    public ResponseEntity<List<StepDefinition>> getAllStepDefinitions() {
        return ResponseEntity.ok(stepDefinitionService.getAllStepDefinitions());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createStepDefinition(@RequestBody StepDefinition stepDef) {
        StepDefinition created = stepDefinitionService.createStepDefinition(stepDef);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stepDefinition", created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stepId}")
    public ResponseEntity<Map<String, Object>> updateStepDefinition(
            @PathVariable String stepId,
            @RequestBody StepDefinition stepDef) {
        StepDefinition updated = stepDefinitionService.updateStepDefinition(stepId, stepDef);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stepDefinition", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{stepId}")
    public ResponseEntity<Map<String, Object>> deleteStepDefinition(@PathVariable String stepId) {
        boolean deleted = stepDefinitionService.deleteStepDefinition(stepId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        return ResponseEntity.ok(response);
    }
}
