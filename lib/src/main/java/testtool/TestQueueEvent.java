package testtool;

import java.util.List;

public class TestQueueEvent {
    private List<String> testQueue;
    private int initialSize;

    public String getCurrentTest() {
        return testQueue.get(0);
    }

    public int getTestsRun() {
        return getInitialSize() - getTestQueue().size();
    }

    public TestQueueEvent() {
    }

    public TestQueueEvent(List<String> testQueue, int initialSize) {
        this.testQueue = testQueue;
        this.initialSize = initialSize;
    }

    public List<String> getTestQueue() {
        return testQueue;
    }

    public int getInitialSize() {
        return initialSize;
    }
}
