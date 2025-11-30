package com.testautomation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.model.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestDataService {
    private static final Logger logger = LoggerFactory.getLogger(TestDataService.class);
    private static final String TEST_DATA_FILE = "src/main/resources/data/test-data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TestDataService() {
        initializeFile();
    }

    private void initializeFile() {
        try {
            File file = new File(TEST_DATA_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                objectMapper.writeValue(file, new ArrayList<TestData>());
            }
        } catch (IOException e) {
            logger.error("Error initializing test data file", e);
        }
    }

    public List<TestData> getAllTestData() {
        try {
            return objectMapper.readValue(new File(TEST_DATA_FILE),
                    new TypeReference<List<TestData>>() {
                    });
        } catch (IOException e) {
            logger.error("Error reading test data", e);
            return new ArrayList<>();
        }
    }

    public Optional<TestData> getTestDataByName(String dataSetName) {
        return getAllTestData().stream()
                .filter(td -> td.getDataSetName().equals(dataSetName))
                .findFirst();
    }

    public List<TestData> getTestDataByCategory(String category) {
        return getAllTestData().stream()
                .filter(td -> td.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<TestData> getTestDataByEnvironment(String environment) {
        return getAllTestData().stream()
                .filter(td -> td.getEnvironment().equals(environment))
                .collect(Collectors.toList());
    }

    public TestData createTestData(TestData testData) {
        List<TestData> allData = getAllTestData();
        if (allData.stream().anyMatch(td -> td.getDataSetName().equals(testData.getDataSetName()))) {
            throw new IllegalArgumentException("Test data already exists: " + testData.getDataSetName());
        }
        allData.add(testData);
        save(allData);
        return testData;
    }

    public TestData updateTestData(String dataSetName, TestData updated) {
        List<TestData> allData = getAllTestData();
        allData.removeIf(td -> td.getDataSetName().equals(dataSetName));
        allData.add(updated);
        save(allData);
        return updated;
    }

    public boolean deleteTestData(String dataSetName) {
        List<TestData> allData = getAllTestData();
        boolean removed = allData.removeIf(td -> td.getDataSetName().equals(dataSetName));
        if (removed) save(allData);
        return removed;
    }

    private void save(List<TestData> data) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(TEST_DATA_FILE), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save test data", e);
        }
    }
}
