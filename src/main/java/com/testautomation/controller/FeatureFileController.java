// Copy each controller to separate file: FeatureFileController.java, PageObjectController.java, etc.

// ==================== FeatureFileController.java ====================
package com.testautomation.controller;

import com.testautomation.model.FeatureFile;
import com.testautomation.service.FeatureFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
public class FeatureFileController {

    @Autowired
    private FeatureFileService featureFileService;

    @GetMapping
    public ResponseEntity<List<FeatureFile>> getAllFeatures() {
        return ResponseEntity.ok(featureFileService.getAllFeatures());
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<FeatureFile> getFeature(@PathVariable String fileName) {
        return ResponseEntity.ok(featureFileService.getFeature(fileName));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createFeature(@RequestBody FeatureFile feature) {
        FeatureFile created = featureFileService.createFeature(feature);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Feature created successfully");
        response.put("feature", created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{fileName}")
    public ResponseEntity<Map<String, Object>> updateFeature(
            @PathVariable String fileName,
            @RequestBody FeatureFile feature) {
        FeatureFile updated = featureFileService.updateFeature(fileName, feature);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Feature updated successfully");
        response.put("feature", updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Map<String, Object>> deleteFeature(@PathVariable String fileName) {
        boolean deleted = featureFileService.deleteFeature(fileName);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted ? "Feature deleted" : "Feature not found");
        return ResponseEntity.ok(response);
    }
}

