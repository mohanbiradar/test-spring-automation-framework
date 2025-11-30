package com.testautomation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService {
    private static final Logger logger = LoggerFactory.getLogger(TagService.class);
    private static final String TAGS_FILE = "src/main/resources/data/tags.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TagService() {
        initializeTagsFile();
        initializeDefaultTags();
    }

    /**
     * Initialize tags storage file
     */
    private void initializeTagsFile() {
        try {
            File tagsFile = new File(TAGS_FILE);
            if (!tagsFile.exists()) {
                tagsFile.getParentFile().mkdirs();
                objectMapper.writeValue(tagsFile, new ArrayList<Tag>());
                logger.info("Tags file initialized: {}", TAGS_FILE);
            }
        } catch (IOException e) {
            logger.error("Error initializing tags file", e);
        }
    }

    /**
     * Initialize default tags if none exist
     */
    private void initializeDefaultTags() {
        try {
            List<Tag> existingTags = getAllTags();
            if (existingTags.isEmpty()) {
                List<Tag> defaultTags = Arrays.asList(
                    new Tag("@smoke", "Quick smoke tests for critical functionality", 
                            Tag.TagType.SMOKE, "#28a745", true),
                    new Tag("@regression", "Full regression test suite", 
                            Tag.TagType.REGRESSION, "#007bff", true),
                    new Tag("@api", "API and backend service tests", 
                            Tag.TagType.API, "#6f42c1", true),
                    new Tag("@ui", "User interface and frontend tests", 
                            Tag.TagType.UI, "#fd7e14", true),
                    new Tag("@integration", "End-to-end integration tests", 
                            Tag.TagType.INTEGRATION, "#20c997", true)
                );
                
                for (Tag tag : defaultTags) {
                    createTag(tag);
                }
                logger.info("Default tags initialized");
            }
        } catch (Exception e) {
            logger.error("Error initializing default tags", e);
        }
    }

    /**
     * Get all tags
     */
    public List<Tag> getAllTags() {
        try {
            File tagsFile = new File(TAGS_FILE);
            if (!tagsFile.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(tagsFile, new TypeReference<List<Tag>>() {});
        } catch (IOException e) {
            logger.error("Error reading tags", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get active tags only
     */
    public List<Tag> getActiveTags() {
        return getAllTags().stream()
                .filter(Tag::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Get tags by type
     */
    public List<Tag> getTagsByType(Tag.TagType type) {
        return getAllTags().stream()
                .filter(tag -> tag.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Get tag by name
     */
    public Optional<Tag> getTagByName(String name) {
        return getAllTags().stream()
                .filter(tag -> tag.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Create new tag
     */
    public Tag createTag(Tag tag) {
        List<Tag> tags = getAllTags();
        
        // Check if tag already exists
        if (tags.stream().anyMatch(t -> t.getName().equalsIgnoreCase(tag.getName()))) {
            throw new IllegalArgumentException("Tag already exists: " + tag.getName());
        }

        tags.add(tag);
        saveTags(tags);
        logger.info("Tag created: {}", tag.getName());
        return tag;
    }

    /**
     * Update existing tag
     */
    public Tag updateTag(String name, Tag updatedTag) {
        List<Tag> tags = getAllTags();
        
        Optional<Tag> existingTag = tags.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();

        if (!existingTag.isPresent()) {
            throw new IllegalArgumentException("Tag not found: " + name);
        }

        tags.remove(existingTag.get());
        tags.add(updatedTag);
        saveTags(tags);
        logger.info("Tag updated: {}", updatedTag.getName());
        return updatedTag;
    }

    /**
     * Delete tag
     */
    public boolean deleteTag(String name) {
        List<Tag> tags = getAllTags();
        boolean removed = tags.removeIf(t -> t.getName().equalsIgnoreCase(name));
        
        if (removed) {
            saveTags(tags);
            logger.info("Tag deleted: {}", name);
        }
        
        return removed;
    }

    /**
     * Toggle tag active status
     */
    public Tag toggleTagStatus(String name) {
        List<Tag> tags = getAllTags();
        
        Optional<Tag> tagOpt = tags.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();

        if (!tagOpt.isPresent()) {
            throw new IllegalArgumentException("Tag not found: " + name);
        }

        Tag tag = tagOpt.get();
        tag.setActive(!tag.isActive());
        saveTags(tags);
        logger.info("Tag status toggled: {} - active: {}", tag.getName(), tag.isActive());
        return tag;
    }

    /**
     * Get tag suggestions based on input
     */
    public List<String> getTagSuggestions(String prefix) {
        return getActiveTags().stream()
                .map(Tag::getName)
                .filter(name -> prefix == null || name.toLowerCase().contains(prefix.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get tag statistics
     */
    public Map<String, Object> getTagStatistics() {
        List<Tag> tags = getAllTags();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalTags", tags.size());
        stats.put("activeTags", tags.stream().filter(Tag::isActive).count());
        stats.put("inactiveTags", tags.stream().filter(t -> !t.isActive()).count());
        
        // Count by type
        Map<Tag.TagType, Long> typeCount = tags.stream()
                .collect(Collectors.groupingBy(Tag::getType, Collectors.counting()));
        stats.put("tagsByType", typeCount);
        
        return stats;
    }

    /**
     * Validate tag names in a list
     */
    public List<String> validateTags(List<String> tagNames) {
        List<String> validTags = new ArrayList<>();
        List<Tag> availableTags = getActiveTags();
        Set<String> availableTagNames = availableTags.stream()
                .map(Tag::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String tagName : tagNames) {
            String normalizedName = tagName.trim().toLowerCase();
            if (!normalizedName.startsWith("@")) {
                normalizedName = "@" + normalizedName;
            }
            
            if (availableTagNames.contains(normalizedName)) {
                validTags.add(normalizedName);
            } else {
                logger.warn("Tag not found or inactive: {}", tagName);
            }
        }

        return validTags;
    }

    /**
     * Save tags to file
     */
    private void saveTags(List<Tag> tags) {
        try {
            File tagsFile = new File(TAGS_FILE);
            tagsFile.getParentFile().mkdirs();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(tagsFile, tags);
        } catch (IOException e) {
            logger.error("Error saving tags", e);
            throw new RuntimeException("Failed to save tags", e);
        }
    }
}
