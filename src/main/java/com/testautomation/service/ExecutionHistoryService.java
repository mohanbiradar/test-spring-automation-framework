package com.testautomation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.testautomation.model.ExecutionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutionHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionHistoryService.class);
    private static final String HISTORY_FILE = "src/main/resources/data/execution-history.json";
    private final ObjectMapper objectMapper;

    public ExecutionHistoryService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initializeFile();
    }

    private void initializeFile() {
        try {
            File file = new File(HISTORY_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                ExecutionHistory history = new ExecutionHistory();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, history);
            }
        } catch (IOException e) {
            logger.error("Error initializing execution history file", e);
        }
    }

    public ExecutionHistory getExecutionHistory() {
        try {
            return objectMapper.readValue(new File(HISTORY_FILE), ExecutionHistory.class);
        } catch (IOException e) {
            logger.error("Error reading execution history", e);
            return new ExecutionHistory();
        }
    }

    public void addExecution(ExecutionHistory.Execution execution) {
        ExecutionHistory history = getExecutionHistory();
        history.getExecutions().add(execution);
        save(history);
        logger.info("Execution added: {}", execution.getExecutionId());
    }

    public List<ExecutionHistory.Execution> getExecutionsByTag(String tag) {
        return getExecutionHistory().getExecutions().stream()
                .filter(e -> e.getTags() != null && e.getTags().contains(tag))
                .sorted(Comparator.comparing(ExecutionHistory.Execution::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<ExecutionHistory.Execution> getRecentExecutions(int limit) {
        return getExecutionHistory().getExecutions().stream()
                .sorted(Comparator.comparing(ExecutionHistory.Execution::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public boolean deleteExecution(String executionId) {
        ExecutionHistory history = getExecutionHistory();
        boolean removed = history.getExecutions().removeIf(e -> e.getExecutionId().equals(executionId));
        if (removed)
            save(history);
        return removed;
    }

    /**
     * Retrieve execution by id.
     */
    public ExecutionHistory.Execution getExecutionById(String executionId) {
        return getExecutionHistory().getExecutions().stream()
                .filter(e -> e.getExecutionId() != null && e.getExecutionId().equals(executionId))
                .findFirst()
                .orElse(null);
    }

    private void save(ExecutionHistory history) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(HISTORY_FILE), history);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save execution history", e);
        }
    }
}
