//package com.testautomation.service;
//
//import com.testautomation.model.FeatureFile;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.nio.file.*;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@Service
//public class FeatureFileService {
//    private static final Logger logger = LoggerFactory.getLogger(FeatureFileService.class);
//    private static final String FEATURES_PATH = "src/test/resources/features";
//    private static final Pattern TAG_PATTERN = Pattern.compile("@\\w+");
//
//    @Autowired
//    private TagService tagService;
//
//    /**
//     * Get all available tags from all feature files
//     */
//    public List<String> getAllAvailableTags() {
//        Set<String> allTags = new HashSet<>();
//
//        try {
//            File featuresDir = new File(FEATURES_PATH);
//            if (featuresDir.exists() && featuresDir.isDirectory()) {
//                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
//
//                if (featureFiles != null) {
//                    for (File file : featureFiles) {
//                        List<String> tags = extractTagsFromFile(file);
//                        allTags.addAll(tags);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error getting available tags", e);
//        }
//
//        return new ArrayList<>(allTags).stream().sorted().collect(Collectors.toList());
//    }
//
//    /**
//     * Get tags for a specific feature file
//     */
//    public List<String> getFeatureTags(String fileName) {
//        try {
//            File featureFile = new File(FEATURES_PATH, fileName);
//            if (featureFile.exists()) {
//                return extractTagsFromFile(featureFile);
//            }
//        } catch (Exception e) {
//            logger.error("Error getting feature tags: {}", fileName, e);
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * Add tags to a feature file
//     */
//    public void addTagsToFeature(String fileName, List<String> tags) {
//        try {
//            File featureFile = new File(FEATURES_PATH, fileName);
//            if (!featureFile.exists()) {
//                throw new IllegalArgumentException("Feature file not found: " + fileName);
//            }
//
//            // Validate tags
//            List<String> validTags = tagService.validateTags(tags);
//            if (validTags.isEmpty()) {
//                logger.warn("No valid tags to add to feature: {}", fileName);
//                return;
//            }
//
//            // Read current content
//            List<String> lines = Files.readAllLines(featureFile.toPath());
//
//            // Find existing tags or insert point
//            int insertIndex = 0;
//            List<String> existingTags = new ArrayList<>();
//
//            for (int i = 0; i < lines.size(); i++) {
//                String line = lines.get(i).trim();
//                if (line.startsWith("@")) {
//                    existingTags.addAll(extractTagsFromLine(line));
//                    insertIndex = i;
//                } else if (line.startsWith("Feature:")) {
//                    if (existingTags.isEmpty()) {
//                        insertIndex = i;
//                    }
//                    break;
//                }
//            }
//
//            // Combine existing and new tags (remove duplicates)
//            Set<String> combinedTags = new HashSet<>(existingTags);
//            combinedTags.addAll(validTags);
//
//            // Remove old tag lines
//            lines.removeIf(line -> line.trim().startsWith("@"));
//
//            // Insert new tag lines
//            String tagLine = String.join(" ", combinedTags);
//            lines.add(insertIndex, tagLine);
//
//            // Write back to file
//            Files.write(featureFile.toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
//            logger.info("Tags added to feature {}: {}", fileName, validTags);
//
//        } catch (Exception e) {
//            logger.error("Error adding tags to feature: {}", fileName, e);
//            throw new RuntimeException("Failed to add tags to feature", e);
//        }
//    }
//
//    /**
//     * Remove tags from a feature file
//     */
//    public void removeTagsFromFeature(String fileName, List<String> tagsToRemove) {
//        try {
//            File featureFile = new File(FEATURES_PATH, fileName);
//            if (!featureFile.exists()) {
//                throw new IllegalArgumentException("Feature file not found: " + fileName);
//            }
//
//            List<String> lines = Files.readAllLines(featureFile.toPath());
//            Set<String> tagsToRemoveSet = tagsToRemove.stream()
//                    .map(String::toLowerCase)
//                    .collect(Collectors.toSet());
//
//            // Process lines
//            List<String> newLines = new ArrayList<>();
//            for (String line : lines) {
//                if (line.trim().startsWith("@")) {
//                    List<String> lineTags = extractTagsFromLine(line);
//                    List<String> remainingTags = lineTags.stream()
//                            .filter(tag -> !tagsToRemoveSet.contains(tag.toLowerCase()))
//                            .collect(Collectors.toList());
//
//                    if (!remainingTags.isEmpty()) {
//                        newLines.add(String.join(" ", remainingTags));
//                    }
//                } else {
//                    newLines.add(line);
//                }
//            }
//
//            Files.write(featureFile.toPath(), newLines, StandardOpenOption.TRUNCATE_EXISTING);
//            logger.info("Tags removed from feature {}: {}", fileName, tagsToRemove);
//
//        } catch (Exception e) {
//            logger.error("Error removing tags from feature: {}", fileName, e);
//            throw new RuntimeException("Failed to remove tags from feature", e);
//        }
//    }
//
//    /**
//     * Get features filtered by tags (AND logic)
//     */
//    public List<String> getFeaturesByTags(List<String> tags) {
//        List<String> matchingFeatures = new ArrayList<>();
//
//        try {
//            File featuresDir = new File(FEATURES_PATH);
//            if (featuresDir.exists() && featuresDir.isDirectory()) {
//                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
//
//                if (featureFiles != null) {
//                    for (File file : featureFiles) {
//                        List<String> fileTags = extractTagsFromFile(file);
//
//                        // Check if file contains all required tags (AND logic)
//                        boolean hasAllTags = tags.stream()
//                                .allMatch(tag -> fileTags.stream()
//                                        .anyMatch(ft -> ft.equalsIgnoreCase(tag)));
//
//                        if (hasAllTags) {
//                            matchingFeatures.add(file.getName());
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error filtering features by tags", e);
//        }
//
//        return matchingFeatures;
//    }
//
//    /**
//     * Get features filtered by tags (OR logic)
//     */
//    public List<String> getFeaturesByTagsOr(List<String> tags) {
//        List<String> matchingFeatures = new ArrayList<>();
//
//        try {
//            File featuresDir = new File(FEATURES_PATH);
//            if (featuresDir.exists() && featuresDir.isDirectory()) {
//                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
//
//                if (featureFiles != null) {
//                    for (File file : featureFiles) {
//                        List<String> fileTags = extractTagsFromFile(file);
//
//                        // Check if file contains any of the required tags (OR logic)
//                        boolean hasAnyTag = tags.stream()
//                                .anyMatch(tag -> fileTags.stream()
//                                        .anyMatch(ft -> ft.equalsIgnoreCase(tag)));
//
//                        if (hasAnyTag) {
//                            matchingFeatures.add(file.getName());
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error filtering features by tags (OR)", e);
//        }
//
//        return matchingFeatures;
//    }
//
//    /**
//     * Get tag usage statistics
//     */
//    public Map<String, Integer> getTagUsageStatistics() {
//        Map<String, Integer> tagUsage = new HashMap<>();
//
//        try {
//            File featuresDir = new File(FEATURES_PATH);
//            if (featuresDir.exists() && featuresDir.isDirectory()) {
//                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
//
//                if (featureFiles != null) {
//                    for (File file : featureFiles) {
//                        List<String> tags = extractTagsFromFile(file);
//                        for (String tag : tags) {
//                            tagUsage.put(tag, tagUsage.getOrDefault(tag, 0) + 1);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error getting tag usage statistics", e);
//        }
//
//        return tagUsage;
//    }
//
//    /**
//     * Extract tags from a file
//     */
//    private List<String> extractTagsFromFile(File file) {
//        List<String> tags = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                if (line.startsWith("@")) {
//                    tags.addAll(extractTagsFromLine(line));
//                } else if (line.startsWith("Feature:")) {
//                    break; // Stop after feature declaration
//                }
//            }
//        } catch (IOException e) {
//            logger.error("Error reading file: {}", file.getName(), e);
//        }
//
//        return tags;
//    }
//
//    /**
//     * Extract tags from a line
//     */
//    private List<String> extractTagsFromLine(String line) {
//        List<String> tags = new ArrayList<>();
//        Matcher matcher = TAG_PATTERN.matcher(line);
//
//        while (matcher.find()) {
//            tags.add(matcher.group());
//        }
//
//        return tags;
//    }
//
//    // Existing methods remain unchanged...
//    // (Keep all your existing FeatureFileService methods here)
//
//    /**
//     * Get all feature files as objects
//     */
//    public List<com.testautomation.model.FeatureFile> getAllFeatures() {
//        List<com.testautomation.model.FeatureFile> features = new ArrayList<>();
//        try {
//            File featuresDir = new File(FEATURES_PATH);
//            if (featuresDir.exists() && featuresDir.isDirectory()) {
//                File[] files = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
//                if (files != null) {
//                    for (File file : files) {
//                        com.testautomation.model.FeatureFile ff = readFeatureFile(file);
//                        if (ff != null) {
//                            features.add(ff);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Error reading all features", e);
//        }
//        return features;
//    }
//
//    private FeatureFile readFeatureFile(File file) {
//        try {
//            FeatureFile ff = new FeatureFile();
//            ff.setFileName(file.getName());
//            List<String> lines = Files.readAllLines(file.toPath());
//            ff.setContent(String.join("\n", lines));
//            // Extract feature name
//            String featureName = lines.stream()
//                    .filter(l -> l.trim().startsWith("Feature:"))
//                    .map(l -> l.replaceFirst("Feature:", "").trim())
//                    .findFirst().orElse("");
//            ff.setFeatureName(featureName);
//
//            // Tags
//            List<String> tags = new ArrayList<>();
//            for (String line : lines) {
//                if (line.trim().startsWith("@")) {
//                    tags.addAll(extractTagsFromLine(line));
//                } else if (line.trim().startsWith("Feature:")) {
//                    break;
//                }
//            }
//            ff.setTags(tags);
//            ff.setScenarioCount((int) lines.stream().filter(l -> l.trim().startsWith("Scenario:")).count());
//            ff.setStepCount((int) lines.stream()
//                    .filter(l -> l.trim().startsWith("Given") || l.trim().startsWith("When")
//                            || l.trim().startsWith("Then") || l.trim().startsWith("And") || l.trim().startsWith("But"))
//                    .count());
//            return ff;
//        } catch (Exception e) {
//            logger.error("Error reading feature file: {}", file.getName(), e);
//            return null;
//        }
//    }
//
//    public FeatureFile getFeature(String fileName) {
//        File f = new File(FEATURES_PATH, fileName);
//        if (!f.exists()) {
//            throw new IllegalArgumentException("Feature not found: " + fileName);
//        }
//        return readFeatureFile(f);
//    }
//
//    public FeatureFile createFeature(FeatureFile feature) {
//        try {
//            String fileName = feature.getFileName();
//            if (fileName == null || fileName.trim().isEmpty()) {
//                fileName = UUID.randomUUID().toString() + ".feature";
//                feature.setFileName(fileName);
//            }
//            File f = new File(FEATURES_PATH, fileName);
//            if (f.exists()) {
//                throw new IllegalArgumentException("Feature file already exists: " + fileName);
//            }
//            Files.createDirectories(f.getParentFile().toPath());
//            Files.write(f.toPath(), feature.getContent().getBytes(), StandardOpenOption.CREATE_NEW);
//            logger.info("Created feature file: {}", fileName);
//            return getFeature(fileName);
//        } catch (Exception e) {
//            logger.error("Error creating feature file", e);
//            throw new RuntimeException("Failed to create feature", e);
//        }
//    }
//
//    public FeatureFile updateFeature(String fileName,
//                                     FeatureFile feature) {
//        try {
//            File f = new File(FEATURES_PATH, fileName);
//            if (!f.exists()) {
//                throw new IllegalArgumentException("Feature file not found: " + fileName);
//            }
//            Files.write(f.toPath(), feature.getContent().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
//            logger.info("Updated feature file: {}", fileName);
//            return getFeature(fileName);
//        } catch (Exception e) {
//            logger.error("Error updating feature file: {}", fileName, e);
//            throw new RuntimeException("Failed to update feature", e);
//        }
//    }
//
//    public boolean deleteFeature(String fileName) {
//        try {
//            File f = new File(FEATURES_PATH, fileName);
//            if (!f.exists()) {
//                return false;
//            }
//            boolean deleted = f.delete();
//            if (deleted) {
//                logger.info("Deleted feature file: {}", fileName);
//            }
//            return deleted;
//        } catch (Exception e) {
//            logger.error("Error deleting feature file: {}", fileName, e);
//            throw new RuntimeException("Failed to delete feature", e);
//        }
//    }
//}

package com.testautomation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.model.FeatureFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FeatureFileService {
    private static final Logger logger = LoggerFactory.getLogger(FeatureFileService.class);
    private static final String FEATURES_PATH = "src/test/resources/features";
    private static final String FEATURES_METADATA_FILE = "src/main/resources/data/features.json";
    private static final Pattern TAG_PATTERN = Pattern.compile("@\\w+");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TagService tagService;

    public FeatureFileService() {
        initializeFeaturesDirectory();
        initializeMetadataFile();
    }

    private void initializeFeaturesDirectory() {
        try {
            Path path = Paths.get(FEATURES_PATH);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Features directory created: {}", FEATURES_PATH);
            }
        } catch (IOException e) {
            logger.error("Error creating features directory", e);
        }
    }

    private void initializeMetadataFile() {
        try {
            File file = new File(FEATURES_METADATA_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                objectMapper.writeValue(file, new ArrayList<FeatureFile>());
                logger.info("Features metadata file initialized");
            }
        } catch (IOException e) {
            logger.error("Error initializing features metadata", e);
        }
    }

    /**
     * Get all features with metadata
     */
    public List<FeatureFile> getAllFeatures() {
        List<FeatureFile> features = new ArrayList<>();

        try {
            File featuresDir = new File(FEATURES_PATH);
            if (!featuresDir.exists() || !featuresDir.isDirectory()) {
                return features;
            }

            File[] files = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));
            if (files != null) {
                for (File file : files) {
                    FeatureFile feature = loadFeatureFile(file);
                    features.add(feature);
                }
            }
        } catch (Exception e) {
            logger.error("Error loading features", e);
        }

        return features;
    }

    /**
     * Get single feature by filename
     */
    public FeatureFile getFeature(String fileName) {
        try {
            File file = new File(FEATURES_PATH, fileName);
            if (file.exists()) {
                return loadFeatureFile(file);
            }
        } catch (Exception e) {
            logger.error("Error loading feature: {}", fileName, e);
        }
        throw new RuntimeException("Feature not found: " + fileName);
    }

    /**
     * Create new feature file
     */
    public FeatureFile createFeature(FeatureFile feature) {
        try {
            if (!feature.getFileName().endsWith(".feature")) {
                throw new IllegalArgumentException("File name must end with .feature");
            }

            File file = new File(FEATURES_PATH, feature.getFileName());
            if (file.exists()) {
                throw new IllegalArgumentException("Feature file already exists: " + feature.getFileName());
            }

            // Set timestamps
            feature.setCreatedDate(LocalDateTime.now());
            feature.setLastModified(LocalDateTime.now());

            // Write content to file
            Files.write(file.toPath(), feature.getContent().getBytes(), StandardOpenOption.CREATE_NEW);

            // Parse and update metadata
            updateFeatureMetadata(feature);

            logger.info("Feature created: {}", feature.getFileName());
            return feature;

        } catch (IOException e) {
            logger.error("Error creating feature", e);
            throw new RuntimeException("Failed to create feature", e);
        }
    }

    /**
     * Update existing feature file
     */
    public FeatureFile updateFeature(String fileName, FeatureFile updatedFeature) {
        try {
            File file = new File(FEATURES_PATH, fileName);
            if (!file.exists()) {
                throw new IllegalArgumentException("Feature file not found: " + fileName);
            }

            // If filename changed, rename the file
            if (!fileName.equals(updatedFeature.getFileName())) {
                File newFile = new File(FEATURES_PATH, updatedFeature.getFileName());
                if (newFile.exists()) {
                    throw new IllegalArgumentException("Target filename already exists");
                }
                file.renameTo(newFile);
                file = newFile;
            }

            // Update timestamp
            updatedFeature.setLastModified(LocalDateTime.now());

            // Write updated content
            Files.write(file.toPath(), updatedFeature.getContent().getBytes(),
                    StandardOpenOption.TRUNCATE_EXISTING);

            // Update metadata
            updateFeatureMetadata(updatedFeature);

            logger.info("Feature updated: {}", updatedFeature.getFileName());
            return updatedFeature;

        } catch (IOException e) {
            logger.error("Error updating feature: {}", fileName, e);
            throw new RuntimeException("Failed to update feature", e);
        }
    }

    /**
     * Delete feature file
     */
    public boolean deleteFeature(String fileName) {
        try {
            File file = new File(FEATURES_PATH, fileName);
            if (file.exists() && file.delete()) {
                logger.info("Feature deleted: {}", fileName);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("Error deleting feature: {}", fileName, e);
            return false;
        }
    }

    /**
     * Load feature file with metadata
     */
    private FeatureFile loadFeatureFile(File file) throws IOException {
        FeatureFile feature = new FeatureFile();
        feature.setFileName(file.getName());

        // Read content
        String content = new String(Files.readAllBytes(file.toPath()));
        feature.setContent(content);

        // Parse feature content
        parseFeatureContent(feature, content);

        // Set file metadata
        feature.setLastModified(LocalDateTime.ofInstant(
                Files.getLastModifiedTime(file.toPath()).toInstant(),
                java.time.ZoneId.systemDefault()));

        return feature;
    }

    /**
     * Parse feature content to extract metadata
     */
    private void parseFeatureContent(FeatureFile feature, String content) {
        String[] lines = content.split("\n");
        List<String> tags = new ArrayList<>();
        List<String> scenarios = new ArrayList<>();
        int scenarioCount = 0;
        int stepCount = 0;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Extract tags
            if (trimmedLine.startsWith("@")) {
                Matcher matcher = TAG_PATTERN.matcher(trimmedLine);
                while (matcher.find()) {
                    String tag = matcher.group();
                    if (!tags.contains(tag)) {
                        tags.add(tag);
                    }
                }
            }

            // Extract feature name
            if (trimmedLine.startsWith("Feature:")) {
                feature.setFeatureName(trimmedLine.substring(8).trim());
            }

            // Extract description (line after Feature:)
            if (feature.getFeatureName() != null && feature.getDescription() == null
                    && !trimmedLine.isEmpty() && !trimmedLine.startsWith("Feature:")
                    && !trimmedLine.startsWith("@") && !trimmedLine.startsWith("Scenario")) {
                feature.setDescription(trimmedLine);
            }

            // Count scenarios
            if (trimmedLine.startsWith("Scenario:") || trimmedLine.startsWith("Scenario Outline:")) {
                scenarioCount++;
                scenarios.add(trimmedLine);
            }

            // Count steps
            if (trimmedLine.startsWith("Given ") || trimmedLine.startsWith("When ")
                    || trimmedLine.startsWith("Then ") || trimmedLine.startsWith("And ")
                    || trimmedLine.startsWith("But ")) {
                stepCount++;
            }
        }

        feature.setTags(tags);
        feature.setScenarios(scenarios);
        feature.setScenarioCount(scenarioCount);
        feature.setStepCount(stepCount);
    }

    /**
     * Update feature metadata
     */
    private void updateFeatureMetadata(FeatureFile feature) {
        parseFeatureContent(feature, feature.getContent());
    }

    // ==================== TAG-RELATED METHODS ====================

    /**
     * Get all available tags from all feature files
     */
    public List<String> getAllAvailableTags() {
        Set<String> allTags = new HashSet<>();

        try {
            File featuresDir = new File(FEATURES_PATH);
            if (featuresDir.exists() && featuresDir.isDirectory()) {
                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));

                if (featureFiles != null) {
                    for (File file : featureFiles) {
                        List<String> tags = extractTagsFromFile(file);
                        allTags.addAll(tags);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting available tags", e);
        }

        return new ArrayList<>(allTags).stream().sorted().collect(Collectors.toList());
    }

    /**
     * Get tags for a specific feature file
     */
    public List<String> getFeatureTags(String fileName) {
        try {
            File featureFile = new File(FEATURES_PATH, fileName);
            if (featureFile.exists()) {
                return extractTagsFromFile(featureFile);
            }
        } catch (Exception e) {
            logger.error("Error getting feature tags: {}", fileName, e);
        }
        return new ArrayList<>();
    }

    /**
     * Add tags to a feature file
     */
    public void addTagsToFeature(String fileName, List<String> tags) {
        try {
            File featureFile = new File(FEATURES_PATH, fileName);
            if (!featureFile.exists()) {
                throw new IllegalArgumentException("Feature file not found: " + fileName);
            }

            // Validate tags
            List<String> validTags = tagService.validateTags(tags);
            if (validTags.isEmpty()) {
                logger.warn("No valid tags to add to feature: {}", fileName);
                return;
            }

            // Read current content
            List<String> lines = Files.readAllLines(featureFile.toPath());

            // Find existing tags or insert point
            int insertIndex = 0;
            List<String> existingTags = new ArrayList<>();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.startsWith("@")) {
                    existingTags.addAll(extractTagsFromLine(line));
                    insertIndex = i;
                } else if (line.startsWith("Feature:")) {
                    if (existingTags.isEmpty()) {
                        insertIndex = i;
                    }
                    break;
                }
            }

            // Combine existing and new tags (remove duplicates)
            Set<String> combinedTags = new HashSet<>(existingTags);
            combinedTags.addAll(validTags);

            // Remove old tag lines
            lines.removeIf(line -> line.trim().startsWith("@"));

            // Insert new tag lines
            String tagLine = String.join(" ", combinedTags);
            lines.add(insertIndex, tagLine);

            // Write back to file
            Files.write(featureFile.toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Tags added to feature {}: {}", fileName, validTags);

        } catch (Exception e) {
            logger.error("Error adding tags to feature: {}", fileName, e);
            throw new RuntimeException("Failed to add tags to feature", e);
        }
    }

    /**
     * Remove tags from a feature file
     */
    public void removeTagsFromFeature(String fileName, List<String> tagsToRemove) {
        try {
            File featureFile = new File(FEATURES_PATH, fileName);
            if (!featureFile.exists()) {
                throw new IllegalArgumentException("Feature file not found: " + fileName);
            }

            List<String> lines = Files.readAllLines(featureFile.toPath());
            Set<String> tagsToRemoveSet = tagsToRemove.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            // Process lines
            List<String> newLines = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().startsWith("@")) {
                    List<String> lineTags = extractTagsFromLine(line);
                    List<String> remainingTags = lineTags.stream()
                            .filter(tag -> !tagsToRemoveSet.contains(tag.toLowerCase()))
                            .collect(Collectors.toList());

                    if (!remainingTags.isEmpty()) {
                        newLines.add(String.join(" ", remainingTags));
                    }
                } else {
                    newLines.add(line);
                }
            }

            Files.write(featureFile.toPath(), newLines, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Tags removed from feature {}: {}", fileName, tagsToRemove);

        } catch (Exception e) {
            logger.error("Error removing tags from feature: {}", fileName, e);
            throw new RuntimeException("Failed to remove tags from feature", e);
        }
    }

    /**
     * Get features filtered by tags (AND logic)
     */
    public List<String> getFeaturesByTags(List<String> tags) {
        List<String> matchingFeatures = new ArrayList<>();

        try {
            File featuresDir = new File(FEATURES_PATH);
            if (featuresDir.exists() && featuresDir.isDirectory()) {
                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));

                if (featureFiles != null) {
                    for (File file : featureFiles) {
                        List<String> fileTags = extractTagsFromFile(file);

                        // Check if file contains all required tags (AND logic)
                        boolean hasAllTags = tags.stream()
                                .allMatch(tag -> fileTags.stream()
                                        .anyMatch(ft -> ft.equalsIgnoreCase(tag)));

                        if (hasAllTags) {
                            matchingFeatures.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error filtering features by tags", e);
        }

        return matchingFeatures;
    }

    /**
     * Get features filtered by tags (OR logic)
     */
    public List<String> getFeaturesByTagsOr(List<String> tags) {
        List<String> matchingFeatures = new ArrayList<>();

        try {
            File featuresDir = new File(FEATURES_PATH);
            if (featuresDir.exists() && featuresDir.isDirectory()) {
                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));

                if (featureFiles != null) {
                    for (File file : featureFiles) {
                        List<String> fileTags = extractTagsFromFile(file);

                        // Check if file contains any of the required tags (OR logic)
                        boolean hasAnyTag = tags.stream()
                                .anyMatch(tag -> fileTags.stream()
                                        .anyMatch(ft -> ft.equalsIgnoreCase(tag)));

                        if (hasAnyTag) {
                            matchingFeatures.add(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error filtering features by tags (OR)", e);
        }

        return matchingFeatures;
    }

    /**
     * Get tag usage statistics
     */
    public Map<String, Integer> getTagUsageStatistics() {
        Map<String, Integer> tagUsage = new HashMap<>();

        try {
            File featuresDir = new File(FEATURES_PATH);
            if (featuresDir.exists() && featuresDir.isDirectory()) {
                File[] featureFiles = featuresDir.listFiles((dir, name) -> name.endsWith(".feature"));

                if (featureFiles != null) {
                    for (File file : featureFiles) {
                        List<String> tags = extractTagsFromFile(file);
                        for (String tag : tags) {
                            tagUsage.put(tag, tagUsage.getOrDefault(tag, 0) + 1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error getting tag usage statistics", e);
        }

        return tagUsage;
    }

    /**
     * Extract tags from a file
     */
    private List<String> extractTagsFromFile(File file) {
        List<String> tags = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("@")) {
                    tags.addAll(extractTagsFromLine(line));
                } else if (line.startsWith("Feature:")) {
                    break; // Stop after feature declaration
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", file.getName(), e);
        }

        return tags;
    }

    /**
     * Extract tags from a line
     */
    private List<String> extractTagsFromLine(String line) {
        List<String> tags = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(line);

        while (matcher.find()) {
            tags.add(matcher.group());
        }

        return tags;
    }
}