package runner.junit5;

import org.junit.platform.commons.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import runner.TestCaseStats;
import runner.TestEvent;
import runner.TestResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.System.currentTimeMillis;
import static runner.TestEvent.methodFailed;

class TestExecutionListenerImpl implements TestExecutionListener {

    private final List<TestEvent> eventsCollected = new ArrayList<>();
    private final Map<TestIdentifier, TestCaseStats> methodStats = new HashMap<>();

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            getMethodStats(testIdentifier).startTime = currentTimeMillis();
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            getMethodStats(testIdentifier).stopTime = currentTimeMillis();
            switch (testExecutionResult.getStatus()) {
                case SUCCESSFUL:
                case ABORTED:
                    break;
                case FAILED: {
                    eventsCollected.add(createEventFrom(testIdentifier, testExecutionResult));
                    break;
                }
                default:
                    throw new PreconditionViolationException(
                        "Unsupported execution status:" + testExecutionResult.getStatus());
            }
        }
    }

    private TestEvent createEventFrom(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        String testCaseName = getTestCaseName(testIdentifier);
        Throwable exception = testExecutionResult.getThrowable().get();

        return methodFailed(testCaseName, getMethodName(testIdentifier), exception);
    }

    private static String getTestCaseName(TestIdentifier description) {
        try {
            TestSource testSource = description.getSource().get();
            if (testSource instanceof MethodSource) {
                return ((MethodSource) testSource).getClassName();
            }
            return description.getDisplayName().split("\\(|\\)")[1];
        } catch (Exception e) {
            return description.getDisplayName();
        }
    }

    public TestResults getTestResults() {
        TestResults results = new TestResults(eventsCollected);
        results.addMethodStats(methodStats.values());
        return results;
    }

    private TestCaseStats getMethodStats(TestIdentifier description) {
        TestCaseStats stats = methodStats.get(description);
        if (Objects.isNull(stats)) {
            stats = new TestCaseStats(getMethodName(description));
            methodStats.put(description, stats);
        }
        return stats;
    }

    private String getMethodName(TestIdentifier description) {
        try {
            TestSource testSource = description.getSource().get();
            if (testSource instanceof MethodSource) {
                return ((MethodSource) testSource).getMethodName();
            }
            return description.getDisplayName().split("\\(|\\)")[0];
        } catch (Exception e) {
            return description.getDisplayName();
        }
    }
}