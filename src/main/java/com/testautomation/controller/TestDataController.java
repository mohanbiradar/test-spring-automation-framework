package com.testautomation.controller;

import com.testautomation.model.TestData;
import com.testautomation.service.TestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-data")
public class TestDataController {

    @Autowired
    private TestDataService testDataService;

    @GetMapping
    public ResponseEntity<List<TestData>> getAllTestData() {
        return ResponseEntity.ok(testDataService.getAllTestData());
    }

    @GetMapping("/{dataSetName}")
    public ResponseEntity<TestData> getTestData(@PathVariable String dataSetName) {
        return testDataService.getTestDataByName(dataSetName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TestData>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(testDataService.getTestDataByCategory(category));
    }

    @GetMapping("/environment/{environment}")
    public ResponseEntity<List<TestData>> getByEnvironment(@PathVariable String environment) {
        return ResponseEntity.ok(testDataService.getTestDataByEnvironment(environment));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createTestData(@RequestBody TestData testData) {
        TestData created = testDataService.createTestData(testData);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("testData", created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{dataSetName}")
    public ResponseEntity<Map<String, Object>> updateTestData(
            @PathVariable String dataSetName,
            @RequestBody TestData testData) {
        TestData updated = testDataService.updateTestData(dataSetName, testData);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("testData", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{dataSetName}")
    public ResponseEntity<Map<String, Object>> deleteTestData(@PathVariable String dataSetName) {
        boolean deleted = testDataService.deleteTestData(dataSetName);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        return ResponseEntity.ok(response);
    }
}
