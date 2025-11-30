package com.testautomation.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a test tag for categorizing and filtering test features
 */
public class Tag {
    private String name;
    private String description;
    private TagType type;
    private String color;
    private boolean active;

    public enum TagType {
        SMOKE("Smoke Tests - Quick validation tests"),
        REGRESSION("Regression Tests - Full feature validation"),
        API("API Tests - Backend service tests"),
        UI("UI Tests - Frontend interface tests"),
        INTEGRATION("Integration Tests - End-to-end workflows"),
        SECURITY("Security Tests - Security validation"),
        PERFORMANCE("Performance Tests - Load and stress tests"),
        CUSTOM("Custom - User-defined category");

        private final String description;

        TagType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Tag() {
        this.active = true;
    }

    @JsonCreator
    public Tag(@JsonProperty("name") String name,
               @JsonProperty("description") String description,
               @JsonProperty("type") TagType type,
               @JsonProperty("color") String color,
               @JsonProperty("active") boolean active) {
        this.name = validateTagName(name);
        this.description = description;
        this.type = type != null ? type : TagType.CUSTOM;
        this.color = color != null ? color : getDefaultColor(this.type);
        this.active = active;
    }

    /**
     * Validates and formats tag name
     * Ensures tag starts with @ and contains no spaces
     */
    private String validateTagName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }

        String trimmedName = name.trim();
        
        // Add @ prefix if not present
        if (!trimmedName.startsWith("@")) {
            trimmedName = "@" + trimmedName;
        }

        // Remove spaces and special characters except underscore and hyphen
        trimmedName = trimmedName.replaceAll("[^@a-zA-Z0-9_-]", "");

        if (trimmedName.length() <= 1) {
            throw new IllegalArgumentException("Tag name must contain valid characters");
        }

        return trimmedName.toLowerCase();
    }

    /**
     * Gets default color for tag type
     */
    private String getDefaultColor(TagType type) {
        switch (type) {
            case SMOKE:
                return "#28a745"; // Green
            case REGRESSION:
                return "#007bff"; // Blue
            case API:
                return "#6f42c1"; // Purple
            case UI:
                return "#fd7e14"; // Orange
            case INTEGRATION:
                return "#20c997"; // Teal
            case SECURITY:
                return "#dc3545"; // Red
            case PERFORMANCE:
                return "#ffc107"; // Yellow
            case CUSTOM:
            default:
                return "#6c757d"; // Gray
        }
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = validateTagName(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TagType getType() {
        return type;
    }

    public void setType(TagType type) {
        this.type = type;
        // Update color to default if not custom
        if (this.color == null || this.color.isEmpty()) {
            this.color = getDefaultColor(type);
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", active=" + active +
                '}';
    }
}
