package runner;

import java.io.Serializable;
import java.util.Objects;

public class TestEvent implements Serializable {

    private final String message;
    private final String name;
    private final String method;
    private final TestState state;
    private boolean isAssertionFailure;
    private StackTraceElement[] stackTrace;
    private String simpleErrorClassName;
    private String fullErrorClassName;

    public TestEvent(TestState eventType, String message, String testName, String testMethod, Throwable error) {
        this.message = message;
        name = testName;
        method = testMethod;
        state = eventType;

        if (error != null) {
            populateAttributesToEnsureSerializability(error);
        }
    }

    private static final long serialVersionUID = -1332435476879803113L;

    public enum TestState {
        METHOD_FAILURE, TEST_CASE_STARTING
    }

    public static TestEvent methodFailed(String message, String testName, String methodName, Throwable throwable) {
        return new TestEvent(TestState.METHOD_FAILURE, message, testName, methodName, throwable);
    }

    public static TestEvent methodFailed(String testName, String methodName, Throwable throwable) {
        return new TestEvent(TestState.METHOD_FAILURE, throwable.getMessage(), testName, methodName, throwable);
    }

    public static TestEvent testCaseStarting(String testClass) {
        return new TestEvent(TestState.TEST_CASE_STARTING, "Test Starting", testClass, "", null);
    }

    private void populateAttributesToEnsureSerializability(Throwable error) {
        isAssertionFailure = isTestFailure(error);
        stackTrace = error.getStackTrace();
        if (stackTrace == null) {
            stackTrace = new StackTraceElement[0];
        }
        simpleErrorClassName = error.getClass().getSimpleName();
        fullErrorClassName = error.getClass().getName();
    }

    private static boolean isTestFailure(Throwable exception) {
        return exception instanceof AssertionError;
    }

    public String getMessage() {
        return null == message ? "" : message;
    }

    public String getTestName() {
        return name;
    }

    public String getTestMethod() {
        return method;
    }

    public boolean isFailure() {
        return isAssertionFailure;
    }

    public TestState getState() {
        return state;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    @Override
    public String toString() {
        return name + "." + method;
    }

    private String getPointOfFailureClass() {
        if (stackTrace.length == 0)
        {
            return fullErrorClassName;
        }
        return getPointOfFailureElement().getClassName();
    }

    private int getPointOfFailureLineNumber() {
        if (stackTrace.length == 0)
        {
            return 0;
        }
        return getPointOfFailureElement().getLineNumber();
    }

    private StackTraceElement getPointOfFailureElement() {
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().equals(method) && element.getClassName().equals(name)) {
                return element;
            }
        }
        return stackTrace[0];
    }

    public PointOfFailure getPointOfFailure() {
        if (getErrorClassName() == null) {
            return null;
        }

        return new PointOfFailure(getPointOfFailureClass(), getPointOfFailureLineNumber(), getErrorClassName(), getMessage());
    }

    public String getErrorClassName() {
        return simpleErrorClassName;
    }
    public String getFullErrorClassName() {
        return fullErrorClassName;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ method.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TestEvent) {
            TestEvent other = (TestEvent) obj;
            return Objects.equals(name, other.name) && Objects.equals(method, other.method);
        }
        return false;
    }
}
