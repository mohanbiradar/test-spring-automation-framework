package com.testautomation.controller;

import com.testautomation.model.Tag;
import com.testautomation.service.FeatureFileService;
import com.testautomation.service.TagService;
import com.testautomation.service.TestExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private FeatureFileService featureFileService;

    @Autowired
    private TestExecutionService testExecutionService;

    /**
     * Get all tags
     */
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            logger.error("Error getting all tags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get active tags only
     */
    @GetMapping("/active")
    public ResponseEntity<List<Tag>> getActiveTags() {
        try {
            List<Tag> tags = tagService.getActiveTags();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            logger.error("Error getting active tags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get tags by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Tag>> getTagsByType(@PathVariable Tag.TagType type) {
        try {
            List<Tag> tags = tagService.getTagsByType(type);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            logger.error("Error getting tags by type", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get tag by name
     */
    @GetMapping("/{name}")
    public ResponseEntity<Tag> getTag(@PathVariable String name) {
        try {
            return tagService.getTagByName(name)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error getting tag: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new tag
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTag(@RequestBody Tag tag) {
        try {
            Tag createdTag = tagService.createTag(tag);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tag created successfully");
            response.put("tag", createdTag);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error creating tag", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create tag");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update existing tag
     */
    @PutMapping("/{name}")
    public ResponseEntity<Map<String, Object>> updateTag(
            @PathVariable String name,
            @RequestBody Tag tag) {
        try {
            Tag updatedTag = tagService.updateTag(name, tag);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tag updated successfully");
            response.put("tag", updatedTag);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Error updating tag: {}", name, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update tag");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete tag
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, Object>> deleteTag(@PathVariable String name) {
        try {
            boolean deleted = tagService.deleteTag(name);
            Map<String, Object> response = new HashMap<>();

            if (deleted) {
                response.put("success", true);
                response.put("message", "Tag deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Tag not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting tag: {}", name, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete tag");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Toggle tag active status
     */
    @PatchMapping("/{name}/toggle")
    public ResponseEntity<Map<String, Object>> toggleTagStatus(@PathVariable String name) {
        try {
            Tag tag = tagService.toggleTagStatus(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tag status updated");
            response.put("tag", tag);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error toggling tag status: {}", name, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to toggle tag status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get tag suggestions
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getTagSuggestions(
            @RequestParam(required = false) String prefix) {
        try {
            List<String> suggestions = tagService.getTagSuggestions(prefix);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            logger.error("Error getting tag suggestions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get tag statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTagStatistics() {
        try {
            Map<String, Object> stats = tagService.getTagStatistics();
            Map<String, Integer> usageStats = featureFileService.getTagUsageStatistics();
            stats.put("tagUsage", usageStats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error getting tag statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get features by tags
     */
    @PostMapping("/features")
    public ResponseEntity<Map<String, Object>> getFeaturesByTags(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) request.get("tags");
            String logic = (String) request.getOrDefault("logic", "AND");

            List<String> features = "OR".equalsIgnoreCase(logic)
                    ? featureFileService.getFeaturesByTagsOr(tags)
                    : featureFileService.getFeaturesByTags(tags);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("features", features);
            response.put("count", features.size());
            response.put("tags", tags);
            response.put("logic", logic);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting features by tags", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get features by tags");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute tests by tags
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeTestsByTags(
            @RequestBody Map<String, Object> request,
            @RequestParam(value = "executionId", required = false) String executionId) {
        try {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) request.get("tags");
            String logic = (String) request.getOrDefault("logic", "AND");

            if (tags == null || tags.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No tags provided");
                return ResponseEntity.badRequest().body(response);
            }

            if (executionId == null || executionId.isBlank()) {
                executionId = testExecutionService.createExecutionId();
            }
            testExecutionService.runTestsByTagsWithId(tags, logic, executionId);
            logger.info("runTestsByTags started with executionId={}", executionId);
            testExecutionService.notifyExecutionStart(executionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test execution started");
            response.put("tags", tags);
            response.put("logic", logic);

            response.put("executionId", executionId);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            logger.error("Error executing tests by tags", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to execute tests: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Execute tests with complex tag logic
     */
    @PostMapping("/execute/complex")
    public ResponseEntity<Map<String, Object>> executeTestsComplexTags(
            @RequestBody Map<String, Object> request,
            @RequestParam(value = "executionId", required = false) String executionId) {
        try {
            @SuppressWarnings("unchecked")
            List<String> includeTags = (List<String>) request.get("includeTags");
            @SuppressWarnings("unchecked")
            List<String> excludeTags = (List<String>) request.get("excludeTags");

            if (includeTags == null || includeTags.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "No include tags provided");
                return ResponseEntity.badRequest().body(response);
            }

            if (executionId == null || executionId.isBlank()) {
                executionId = testExecutionService.createExecutionId();
            }
            testExecutionService.runTestsByComplexTagsWithId(includeTags, excludeTags, executionId);
            logger.info("runTestsByComplexTags started with executionId={}", executionId);
            testExecutionService.notifyExecutionStart(executionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Test execution started");
            response.put("includeTags", includeTags);
            response.put("excludeTags", excludeTags);

            response.put("executionId", executionId);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            logger.error("Error executing tests with complex tags", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to execute tests: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add tags to feature
     */
    @PostMapping("/features/{fileName}/tags")
    public ResponseEntity<Map<String, Object>> addTagsToFeature(
            @PathVariable String fileName,
            @RequestBody Map<String, List<String>> request) {
        try {
            List<String> tags = request.get("tags");
            featureFileService.addTagsToFeature(fileName, tags);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tags added successfully");
            response.put("fileName", fileName);
            response.put("tags", tags);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error adding tags to feature", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add tags: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove tags from feature
     */
    @DeleteMapping("/features/{fileName}/tags")
    public ResponseEntity<Map<String, Object>> removeTagsFromFeature(
            @PathVariable String fileName,
            @RequestBody Map<String, List<String>> request) {
        try {
            List<String> tags = request.get("tags");
            featureFileService.removeTagsFromFeature(fileName, tags);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tags removed successfully");
            response.put("fileName", fileName);
            response.put("tags", tags);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error removing tags from feature", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to remove tags: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
