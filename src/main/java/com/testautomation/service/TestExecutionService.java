
package com.testautomation.service;

import com.testautomation.model.ExecutionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class TestExecutionService {
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionService.class);
    private static final String REPORTS_DIR = "src/main/resources/reports/cucumber";

    @Value("${maven.home:}")
    private String mavenHome;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ExecutionHistoryService executionHistoryService;

    @Autowired
    private FeatureFileService featureFileService;

    @Autowired
    private TagService tagService;

    @Value("${test.execution.maven.command:mvn}")
    private String configuredMavenCommand;
    @Value("${test.execution.timeout.seconds:900}")
    private int executionTimeoutSeconds;

    // Track running processes by executionId to enable cancellation
    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

    /**
     * Execute Maven command using ProcessBuilder
     */
    private Process executeMavenCommand(List<String> arguments) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder();

        List<String> command = new ArrayList<>();
        command.add(getMavenCommand());
        command.addAll(arguments);

        processBuilder.command(command);
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectErrorStream(true);

        logger.info("Executing command: {}", String.join(" ", command));

        return processBuilder.start();
    }

    /**
     * Run all tests
     */
    public CompletableFuture<ExecutionHistory.Execution> runAllTests() {
        String executionId = generateExecutionId();
        return runAllTestsWithId(executionId);
    }

    public CompletableFuture<ExecutionHistory.Execution> runAllTestsWithId(String executionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting execution of all tests (executionId={})", executionId);
                sendProgressUpdate(executionId, "Preparing to run all tests...", 0);
                // Pre-check: ensure we have feature files before running maven
                List<com.testautomation.model.FeatureFile> allFeatures = featureFileService.getAllFeatures();
                if (allFeatures == null || allFeatures.isEmpty()) {
                    String execId = executionId;
                    ExecutionHistory.Execution skippedExec = new ExecutionHistory.Execution();
                    skippedExec.setExecutionId(execId);
                    skippedExec.setExecutionType("ALL");
                    skippedExec.setStatus("SKIPPED");
                    skippedExec.setTimestamp(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    skippedExec.setNotes("No feature files found to execute");
                    executionHistoryService.addExecution(skippedExec);
                    sendProgressUpdate(executionId, "No feature files found. Execution skipped.", 100);
                    return skippedExec;
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                ExecutionHistory.Execution execution = new ExecutionHistory.Execution();
                execution.setExecutionId(executionId);
                execution.setExecutionType("ALL");
                execution.setTimestamp(timestamp);
                execution.setStatus("RUNNING");

                List<String> mavenArgs = Arrays.asList(
                        "clean", "test",
                        "-Dcucumber.plugin=html:" + REPORTS_DIR + "/cucumber-report-" + executionId + ".html");

                sendProgressUpdate(executionId, "Executing all tests...", 20);

                Process process = executeMavenCommand(mavenArgs);
                runningProcesses.put(executionId, process);
                Thread heartbeat = new Thread(() -> {
                    try {
                        while (process.isAlive()) {
                            sendProgressUpdate(executionId, "Execution in progress...", 40);
                            Thread.sleep(10000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
                heartbeat.setDaemon(true);
                heartbeat.start();
                // Timeout watchdog, to prevent infinite execution
                Thread watchdog = new Thread(() -> {
                    try {
                        if (!process.waitFor(executionTimeoutSeconds, TimeUnit.SECONDS)) {
                            sendProgressUpdate(executionId, "Execution timed out", -1);
                            logger.warn("Process exceeded timeout ({}s); destroying", executionTimeoutSeconds);
                            process.destroyForcibly();
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
                watchdog.setDaemon(true);
                watchdog.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    int progress = 30;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        logger.info(line);

                        if (line.contains("Scenario:")) {
                            progress = Math.min(progress + 2, 90);
                            sendProgressUpdate(executionId, "Running: " + line, progress);
                        } else if (line.contains("BUILD SUCCESS")) {
                            progress = 95;
                            sendProgressUpdate(executionId, "Build succeeded", progress);
                        } else if (line.contains("BUILD FAILURE")) {
                            progress = -1;
                            sendProgressUpdate(executionId, "Build failed", progress);
                        }
                    }
                }

                int exitCode = process.waitFor();
                runningProcesses.remove(executionId);
                logger.info("Execution finished. Exit code: {}", exitCode);

                sendProgressUpdate(executionId, "Processing results...", 95);

                execution.setStatus(exitCode == 0 ? "PASSED" : "FAILED");
                execution.setDuration(calculateDuration(output.toString()));
                java.nio.file.Path reportPath = java.nio.file.Paths.get(REPORTS_DIR,
                        "cucumber-report-" + executionId + ".html");
                if (java.nio.file.Files.exists(reportPath)) {
                    execution.setReportPath(reportPath.toString());
                } else {
                    logger.warn("Expected report not found at {}", reportPath.toAbsolutePath());
                    execution.setNotes("Report not generated: " + reportPath.toAbsolutePath());
                    sendProgressUpdate(executionId, "Report not generated: " + reportPath.toAbsolutePath(), 99);
                }

                Map<String, Object> results = parseTestResults(output.toString());
                execution.setTotalScenarios((Integer) results.get("totalScenarios"));
                execution.setPassedScenarios((Integer) results.get("passedScenarios"));
                execution.setFailedScenarios((Integer) results.get("failedScenarios"));
                execution.setTotalSteps((Integer) results.get("totalSteps"));
                execution.setPassedSteps((Integer) results.get("passedSteps"));
                execution.setFailedSteps((Integer) results.get("failedSteps"));

                // If no scenarios executed, mark as SKIPPED
                if (execution.getTotalScenarios() == 0) {
                    execution.setStatus("SKIPPED");
                    execution.setNotes(execution.getNotes() == null ? "No scenarios executed" : execution.getNotes());
                }
                executionHistoryService.addExecution(execution);

                sendProgressUpdate(executionId, "Execution completed!", 100);
                logger.info("Test execution completed: {}", executionId);

                return execution;

            } catch (Exception e) {
                logger.error("Error executing all tests", e);
                sendProgressUpdate(executionId, "Execution failed: " + e.getMessage(), -1);

                // Return failed execution record
                ExecutionHistory.Execution failedExecution = new ExecutionHistory.Execution();
                failedExecution.setExecutionId(generateExecutionId());
                failedExecution.setExecutionType("ALL");
                failedExecution.setStatus("FAILED");
                failedExecution
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                return failedExecution;
            }
        });
    }

    /**
     * Run specific feature file
     */
    public CompletableFuture<ExecutionHistory.Execution> runFeatureFile(String fileName) {
        String executionId = generateExecutionId();
        return runFeatureFileWithId(fileName, executionId);
    }

    public CompletableFuture<ExecutionHistory.Execution> runFeatureFileWithId(String fileName, String executionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting execution of feature: {} (executionId={})", fileName, executionId);
                sendProgressUpdate(executionId, "Preparing to run feature: " + fileName, 0);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                ExecutionHistory.Execution execution = new ExecutionHistory.Execution();
                execution.setExecutionId(executionId);
                execution.setExecutionType("FEATURE");
                execution.setFeatureFiles(Arrays.asList(fileName));
                execution.setTimestamp(timestamp);
                execution.setStatus("RUNNING");

                String featurePath = "src/test/resources/features/" + fileName;
                List<String> mavenArgs = Arrays.asList(
                        "test",
                        "-Dcucumber.features=" + featurePath,
                        "-Dcucumber.plugin=html:" + REPORTS_DIR + "/cucumber-report-" + executionId + ".html");

                sendProgressUpdate(executionId, "Executing feature: " + fileName, 20);

                Process process = executeMavenCommand(mavenArgs);
                runningProcesses.put(executionId, process);
                Thread heartbeat = new Thread(() -> {
                    try {
                        while (process.isAlive()) {
                            sendProgressUpdate(executionId, "Execution in progress...", 40);
                            Thread.sleep(10000);
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
                heartbeat.setDaemon(true);
                heartbeat.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    int progress = 30;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        logger.info(line);

                        if (line.contains("Scenario:")) {
                            progress = Math.min(progress + 10, 90);
                            sendProgressUpdate(executionId, "Running: " + line, progress);
                        } else if (line.contains("BUILD SUCCESS")) {
                            progress = 95;
                            sendProgressUpdate(executionId, "Build succeeded", progress);
                        } else if (line.contains("BUILD FAILURE")) {
                            progress = -1;
                            sendProgressUpdate(executionId, "Build failed", progress);
                        }
                    }
                }

                int exitCode = process.waitFor();
                runningProcesses.remove(executionId);
                logger.info("Execution finished. Exit code: {}", exitCode);

                sendProgressUpdate(executionId, "Processing results...", 95);

                execution.setStatus(exitCode == 0 ? "PASSED" : "FAILED");
                execution.setDuration(calculateDuration(output.toString()));
                java.nio.file.Path fileReportPath = java.nio.file.Paths.get(REPORTS_DIR,
                        "cucumber-report-" + executionId + ".html");
                if (java.nio.file.Files.exists(fileReportPath)) {
                    execution.setReportPath(fileReportPath.toString());
                } else {
                    logger.warn("Report not found after feature execution: {}", fileReportPath.toAbsolutePath());
                    execution.setNotes("Report not generated: " + fileReportPath.toAbsolutePath());
                    sendProgressUpdate(executionId, "Report not generated: " + fileReportPath.toAbsolutePath(), 99);
                }

                Map<String, Object> results = parseTestResults(output.toString());
                execution.setTotalScenarios((Integer) results.get("totalScenarios"));
                execution.setPassedScenarios((Integer) results.get("passedScenarios"));
                execution.setFailedScenarios((Integer) results.get("failedScenarios"));
                execution.setTotalSteps((Integer) results.get("totalSteps"));
                execution.setPassedSteps((Integer) results.get("passedSteps"));
                execution.setFailedSteps((Integer) results.get("failedSteps"));

                if (execution.getTotalScenarios() == 0) {
                    execution.setStatus("SKIPPED");
                    execution.setNotes(execution.getNotes() == null ? "No scenarios executed" : execution.getNotes());
                }
                executionHistoryService.addExecution(execution);

                sendProgressUpdate(executionId, "Execution completed!", 100);
                logger.info("Feature execution completed: {}", executionId);

                return execution;

            } catch (Exception e) {
                logger.error("Error executing feature: {}", fileName, e);
                sendProgressUpdate(executionId, "Execution failed: " + e.getMessage(), -1);

                ExecutionHistory.Execution failedExecution = new ExecutionHistory.Execution();
                failedExecution.setExecutionId(generateExecutionId());
                failedExecution.setExecutionType("FEATURE");
                failedExecution.setFeatureFiles(Arrays.asList(fileName));
                failedExecution.setStatus("FAILED");
                failedExecution
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                return failedExecution;
            }
        });
    }

    /**
     * Execute tests by tags
     */
    public CompletableFuture<ExecutionHistory.Execution> runTestsByTags(List<String> tags, String tagLogic) {
        String executionId = generateExecutionId();
        return runTestsByTagsWithId(tags, tagLogic, executionId);
    }

    public CompletableFuture<ExecutionHistory.Execution> runTestsByTagsWithId(List<String> tags, String tagLogic,
            String executionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting test execution by tags: {} (Logic: {}) (executionId={})", tags, tagLogic,
                        executionId);
                sendProgressUpdate(executionId, "Preparing test execution...", 0);

                List<String> validTags = tagService.validateTags(tags);
                if (validTags.isEmpty()) {
                    throw new RuntimeException("No valid tags found: " + tags);
                }

                List<String> matchingFeatures = "OR".equalsIgnoreCase(tagLogic)
                        ? featureFileService.getFeaturesByTagsOr(validTags)
                        : featureFileService.getFeaturesByTags(validTags);

                if (matchingFeatures.isEmpty()) {
                    String execId = generateExecutionId();
                    ExecutionHistory.Execution skippedExec = new ExecutionHistory.Execution();
                    skippedExec.setExecutionId(execId);
                    skippedExec.setExecutionType("TAG_BASED");
                    skippedExec.setTags(validTags);
                    skippedExec.setStatus("SKIPPED");
                    skippedExec.setTimestamp(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    skippedExec.setNotes("No matching features found for tags: " + validTags);
                    executionHistoryService.addExecution(skippedExec);
                    sendProgressUpdate(executionId, "No matching features found; execution skipped.", 100);
                    return skippedExec;
                }

                logger.info("Found {} features matching tags: {}", matchingFeatures.size(), validTags);
                sendProgressUpdate(executionId, "Found " + matchingFeatures.size() + " features to execute", 10);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                ExecutionHistory.Execution execution = new ExecutionHistory.Execution();
                execution.setExecutionId(executionId);
                execution.setExecutionType("TAG_BASED");
                execution.setTags(validTags);
                execution.setTagLogic(tagLogic);
                execution.setFeatureFiles(matchingFeatures);
                execution.setTimestamp(timestamp);
                execution.setStatus("RUNNING");

                String tagExpression = buildTagExpression(validTags, tagLogic);
                List<String> mavenArgs = Arrays.asList(
                        "test",
                        "-Dcucumber.filter.tags=" + tagExpression,
                        "-Dcucumber.plugin=html:" + REPORTS_DIR + "/cucumber-report-" + executionId + ".html");

                sendProgressUpdate(executionId, "Executing tests with tags: " + String.join(", ", validTags), 20);

                Process process = executeMavenCommand(mavenArgs);
                runningProcesses.put(executionId, process);

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    int progress = 30;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        logger.info(line);

                        if (line.contains("Scenario:")) {
                            progress = Math.min(progress + 5, 90);
                            sendProgressUpdate(executionId, "Running: " + line, progress);
                        } else if (line.contains("BUILD SUCCESS")) {
                            progress = 95;
                            sendProgressUpdate(executionId, "Build succeeded", progress);
                        } else if (line.contains("BUILD FAILURE")) {
                            progress = -1;
                            sendProgressUpdate(executionId, "Build failed", progress);
                        }
                    }
                }

                int exitCode = process.waitFor();
                runningProcesses.remove(executionId);
                logger.info("Execution finished. Exit code: {}", exitCode);

                sendProgressUpdate(executionId, "Processing results...", 95);

                execution.setStatus(exitCode == 0 ? "PASSED" : "FAILED");
                java.nio.file.Path featureReportPath = java.nio.file.Paths.get(REPORTS_DIR,
                        "cucumber-report-" + executionId + ".html");
                if (java.nio.file.Files.exists(featureReportPath)) {
                    execution.setReportPath(featureReportPath.toString());
                } else {
                    logger.warn("Report not found after feature execution: {}", featureReportPath.toAbsolutePath());
                    execution.setNotes("Report not generated: " + featureReportPath.toAbsolutePath());
                    sendProgressUpdate(executionId, "Report not generated: " + featureReportPath.toAbsolutePath(), 99);
                }
                execution.setDuration(calculateDuration(output.toString()));

                Map<String, Object> results = parseTestResults(output.toString());
                execution.setTotalScenarios((Integer) results.get("totalScenarios"));
                execution.setPassedScenarios((Integer) results.get("passedScenarios"));
                execution.setFailedScenarios((Integer) results.get("failedScenarios"));
                execution.setTotalSteps((Integer) results.get("totalSteps"));
                execution.setPassedSteps((Integer) results.get("passedSteps"));
                execution.setFailedSteps((Integer) results.get("failedSteps"));

                executionHistoryService.addExecution(execution);

                sendProgressUpdate(executionId, "Execution completed!", 100);
                logger.info("Test execution completed: {}", executionId);

                return execution;

            } catch (Exception e) {
                logger.error("Error executing tests by tags", e);
                sendProgressUpdate(executionId, "Execution failed: " + e.getMessage(), -1);
                throw new RuntimeException("Test execution failed", e);
            }
        });
    }

    private String buildTagExpression(List<String> tags, String logic) {
        if (tags.isEmpty())
            return "";
        return "OR".equalsIgnoreCase(logic)
                ? String.join(" or ", tags)
                : String.join(" and ", tags);
    }

    private String generateExecutionId() {
        return "exec_" + System.currentTimeMillis();
    }

    /**
     * Public method to create an execution id for callers (controllers)
     */
    public String createExecutionId() {
        return generateExecutionId();
    }

    private String calculateDuration(String output) {
        try {
            if (output.contains("m ") && output.contains("s")) {
                int minutesIndex = output.lastIndexOf("m ");
                int secondsIndex = output.lastIndexOf("s");
                if (minutesIndex > 0 && secondsIndex > minutesIndex) {
                    String timeSection = output.substring(Math.max(0, minutesIndex - 10), secondsIndex + 1);
                    return timeSection.trim();
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse duration", e);
        }
        return "N/A";
    }

    private Map<String, Object> parseTestResults(String output) {
        Map<String, Object> results = new HashMap<>();
        results.put("totalScenarios", 0);
        results.put("passedScenarios", 0);
        results.put("failedScenarios", 0);
        results.put("totalSteps", 0);
        results.put("passedSteps", 0);
        results.put("failedSteps", 0);
        return results;
    }

    /**
     * Send progress update with an executionId so clients can filter messages.
     */
    private void sendProgressUpdate(String executionId, String message, int progress) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("executionId", executionId);
            update.put("message", message);
            update.put("progress", progress);
            update.put("timestamp", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/execution-progress", update);
        } catch (Exception e) {
            logger.warn("Failed to send progress update", e);
        }
    }

    public CompletableFuture<ExecutionHistory.Execution> runTestsByComplexTags(
            List<String> includeTags, List<String> excludeTags) {
        String executionId = generateExecutionId();
        return runTestsByComplexTagsWithId(includeTags, excludeTags, executionId);
    }

    public CompletableFuture<ExecutionHistory.Execution> runTestsByComplexTagsWithId(List<String> includeTags,
            List<String> excludeTags, String executionId) {
        // Not implementing exclusion at the moment, reuse includeTags with AND logic
        return runTestsByTagsWithId(includeTags, "AND", executionId);
    }

    /**
     * Returns the maven command to use (prefer mvnw wrapper if present)
     */
    private String getMavenCommand() {
        try {
            String cwd = System.getProperty("user.dir");
            java.io.File mvnwCmd = new java.io.File(cwd, "mvnw.cmd");
            if (mvnwCmd.exists() && mvnwCmd.canExecute()) {
                return mvnwCmd.getAbsolutePath();
            }
            java.io.File mvnw = new java.io.File(cwd, "mvnw");
            if (mvnw.exists() && mvnw.canExecute()) {
                return mvnw.getAbsolutePath();
            }
        } catch (Exception ignored) {
        }
        // Try to detect mvn on PATH
        try {
            String detected = detectMavenFromPath();
            if (detected != null)
                return detected;
        } catch (Exception ignored) {
        }

        // Check environment variable M2_HOME or MAVEN_HOME
        String m2 = System.getenv("M2_HOME");
        if (m2 != null && !m2.isBlank()) {
            java.io.File f = new java.io.File(m2, "bin/mvn.cmd");
            if (f.exists())
                return f.getAbsolutePath();
            f = new java.io.File(m2, "bin/mvn");
            if (f.exists())
                return f.getAbsolutePath();
        }
        String mvHome = System.getenv("MAVEN_HOME");
        if (mvHome != null && !mvHome.isBlank()) {
            java.io.File f = new java.io.File(mvHome, "bin/mvn.cmd");
            if (f.exists())
                return f.getAbsolutePath();
            f = new java.io.File(mvHome, "bin/mvn");
            if (f.exists())
                return f.getAbsolutePath();
        }

        // Try common installation locations (Windows)
        String pf = System.getenv("ProgramFiles");
        String pf86 = System.getenv("ProgramFiles(x86)");
        String[] candidates = new String[] { pf, pf86, "C:\\Program Files", "C:\\Program Files (x86)" };
        for (String base : candidates) {
            if (base == null)
                continue;
            java.io.File baseDir = new java.io.File(base);
            java.io.File[] dirs = baseDir
                    .listFiles((f) -> f.isDirectory() && f.getName().toLowerCase().contains("maven"));
            if (dirs != null) {
                for (java.io.File d : dirs) {
                    java.io.File mvnCmd = new java.io.File(d, "bin\\mvn.cmd");
                    if (mvnCmd.exists())
                        return mvnCmd.getAbsolutePath();
                    java.io.File mvn = new java.io.File(d, "bin/mvn");
                    if (mvn.exists())
                        return mvn.getAbsolutePath();
                }
            }
        }

        // fallback to configured command (default: mvn)
        return configuredMavenCommand != null && !configuredMavenCommand.isBlank() ? configuredMavenCommand : "mvn";
    }

    private String detectMavenFromPath() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        ProcessBuilder pb = isWindows ? new ProcessBuilder("cmd", "/c", "where mvn")
                : new ProcessBuilder("sh", "-c", "which mvn");
        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();
            java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
            String line = r.readLine();
            if (line != null && !line.isBlank()) {
                // Trim trailing spaces and return
                return line.trim();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Validates that the configured maven command is available on the current
     * environment; otherwise throws an exception
     */
    private void validateMavenCommandAvailable() {
        String cmd = getMavenCommand();
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd, "-v");
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            boolean finished = proc.waitFor(3, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished || proc.exitValue() != 0) {
                throw new IllegalStateException("Maven command not available or returned non-zero: " + cmd);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Maven command not found: '" + cmd
                    + "'. Configure 'test.execution.maven.command' in application.properties or use a maven wrapper (mvnw/mvnw.cmd) in the project root.",
                    e);
        }
    }

    public boolean isMavenAvailable() {
        try {
            validateMavenCommandAvailable();
            return true;
        } catch (IllegalStateException e) {
            logger.warn("Maven not available: {}", e.getMessage());
            return false;
        }
    }

    public String getMavenCommandPath() {
        return getMavenCommand();
    }

    /**
     * Public helper used by controllers to notify that execution is starting.
     */
    public void notifyExecutionStart(String executionId) {
        sendProgressUpdate(executionId, "Execution started", 1);
    }

    /**
     * Attempt to cancel a running execution by id. Returns true if cancelled.
     */
    public boolean cancelExecution(String executionId) {
        Process process = runningProcesses.get(executionId);
        if (process == null) {
            return false;
        }
        try {
            process.destroyForcibly();
            sendProgressUpdate(executionId, "Execution cancelled by user", -1);
            runningProcesses.remove(executionId);
            return true;
        } catch (Exception e) {
            logger.warn("Failed to cancel execution {}: {}", executionId, e.getMessage());
            return false;
        }
    }
}
