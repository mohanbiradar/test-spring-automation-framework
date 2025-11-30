package com.testautomation.controller;

import com.testautomation.model.PageObject;
import com.testautomation.service.PageObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/page-objects")
public class PageObjectController {

    @Autowired
    private PageObjectService pageObjectService;

    @GetMapping
    public ResponseEntity<List<PageObject>> getAllPageObjects() {
        return ResponseEntity.ok(pageObjectService.getAllPageObjects());
    }

    @GetMapping("/{pageName}")
    public ResponseEntity<PageObject> getPageObject(@PathVariable String pageName) {
        return pageObjectService.getPageObjectByName(pageName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPageObject(@RequestBody PageObject pageObject) {
        PageObject created = pageObjectService.createPageObject(pageObject);
        pageObjectService.savePageObjectClass(created);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Page object created");
        response.put("pageObject", created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{pageName}")
    public ResponseEntity<Map<String, Object>> updatePageObject(
            @PathVariable String pageName,
            @RequestBody PageObject pageObject) {
        PageObject updated = pageObjectService.updatePageObject(pageName, pageObject);
        pageObjectService.savePageObjectClass(updated);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Page object updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{pageName}")
    public ResponseEntity<Map<String, Object>> deletePageObject(@PathVariable String pageName) {
        boolean deleted = pageObjectService.deletePageObject(pageName);
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("message", deleted ? "Page object deleted" : "Not found");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{pageName}/generate")
    public ResponseEntity<Map<String, Object>> generateCode(@PathVariable String pageName) {
        return pageObjectService.getPageObjectByName(pageName)
                .map(po -> {
                    String code = pageObjectService.generatePageObjectClass(po);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("code", code);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
