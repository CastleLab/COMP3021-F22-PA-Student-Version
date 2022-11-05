package hk.ust.comp3021.utils;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;
import static org.junit.platform.launcher.TagFilter.includeTags;

public class Grader {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java hk.ust.comp3021.utils.Grader <output dir>");
            System.exit(1);
            return;
        }
        final var outputDir = new File(args[0]);
        if (!outputDir.exists()) {
            System.out.println("Output directory does not exist");
            System.exit(1);
            return;
        }

        final var repeat = 10;
        final var publicSummary = runTests(TestKind.PUBLIC, repeat);
        outputSummary(publicSummary, new File(outputDir, "public-tests.tsv"));
        final var hiddenSummary = runTests(TestKind.HIDDEN, repeat);
        outputSummary(hiddenSummary, new File(outputDir, "hidden-tests.tsv"));
        System.out.println("Done");
    }

    private static void outputSummary(TestSummary summary, File outputFile) throws IOException {
        final var csvBuilder = new StringBuilder();
        csvBuilder.append(summary.tests.stream().map(TestIdentifier::getUniqueId).collect(Collectors.joining("\t")));
        csvBuilder.append(System.lineSeparator());
        summary.results.forEach(result -> {
            final var line = summary.tests.stream().map(test -> {
                final var testResult = result.get(test);
                if (testResult == null) {
                    return "0";
                } else if (testResult) {
                    return "1";
                } else {
                    return "-1";
                }
            }).collect(Collectors.joining("\t"));
            csvBuilder.append(line);
            csvBuilder.append(System.lineSeparator());
        });
        try (final var writer = new FileWriter(outputFile)) {
            writer.write(csvBuilder.toString());
        }
    }

    private static TestSummary runTests(String tag, int repeat) {
        final var summary = new TestSummary();
        for (int i = 0; i < repeat; i++) {
            summary.nextRound();
            final var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                    selectPackage("hk.ust.comp3021")
                )
                .filters(
                    includeTags(tag)
                )
                .build();
            final var listener = new SummaryGeneratingListener();

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                // Register a listener of your choice
                launcher.registerTestExecutionListeners(summary);
                launcher.registerTestExecutionListeners(listener);
                // Discover tests and build a test plan
                TestPlan testPlan = launcher.discover(request);
                // Execute test plan
                launcher.execute(testPlan);
            }
        }
        return summary;
    }
}

class TestSummary implements TestExecutionListener {

    public final List<TestIdentifier> tests = new ArrayList<>();
    public final List<Map<TestIdentifier, Boolean>> results = new ArrayList<>();

    private Map<TestIdentifier, Boolean> currentResult;

    public void nextRound() {
        currentResult = new ConcurrentHashMap<>();
        results.add(currentResult);
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (tests.contains(testIdentifier)) {
            return;
        }
        if (testIdentifier.isTest()) {
            tests.add(testIdentifier);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        switch (testExecutionResult.getStatus()) {
            case SUCCESSFUL -> currentResult.put(testIdentifier, true);
            case FAILED, ABORTED, default -> currentResult.put(testIdentifier, false);
        }
    }
}
