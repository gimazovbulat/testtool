package runner;


import runner.junit5.Runner;

public class RunnerImpl {

    public TestResults runTest(String testClassName) {
        Class<?> testClass;
        try {
            testClass = Class.forName(testClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(testClassName);
        }

        return new Runner().runTest(testClass);
    }
}
