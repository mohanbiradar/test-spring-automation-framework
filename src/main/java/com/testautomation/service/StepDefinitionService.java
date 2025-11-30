package com.testautomation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.model.StepDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StepDefinitionService {
    private static final Logger logger = LoggerFactory.getLogger(StepDefinitionService.class);
    private static final String STEP_DEFS_FILE = "src/main/resources/data/step-definitions.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StepDefinitionService() {
        initializeFile();
    }

    private void initializeFile() {
        try {
            File file = new File(STEP_DEFS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                objectMapper.writeValue(file, new ArrayList<StepDefinition>());
            }
        } catch (IOException e) {
            logger.error("Error initializing step definitions file", e);
        }
    }

    public List<StepDefinition> getAllStepDefinitions() {
        try {
            return objectMapper.readValue(new File(STEP_DEFS_FILE), 
                new TypeReference<List<StepDefinition>>() {});
        } catch (IOException e) {
            logger.error("Error reading step definitions", e);
            return new ArrayList<>();
        }
    }

    public StepDefinition createStepDefinition(StepDefinition stepDef) {
        List<StepDefinition> stepDefs = getAllStepDefinitions();
        stepDefs.add(stepDef);
        save(stepDefs);
        return stepDef;
    }

    public StepDefinition updateStepDefinition(String stepId, StepDefinition updated) {
        List<StepDefinition> stepDefs = getAllStepDefinitions();
        stepDefs.removeIf(sd -> sd.getStepId().equals(stepId));
        stepDefs.add(updated);
        save(stepDefs);
        return updated;
    }

    public boolean deleteStepDefinition(String stepId) {
        List<StepDefinition> stepDefs = getAllStepDefinitions();
        boolean removed = stepDefs.removeIf(sd -> sd.getStepId().equals(stepId));
        if (removed) save(stepDefs);
        return removed;
    }

    private void save(List<StepDefinition> stepDefs) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(STEP_DEFS_FILE), stepDefs);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save step definitions", e);
        }
    }
}


