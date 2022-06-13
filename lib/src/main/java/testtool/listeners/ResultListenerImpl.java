package testtool.listeners;

import testtool.data.TestCaseEvent;

public class ResultListenerImpl implements ResultsListener {

    private final TestStats testStats = new TestStats();

    /**
     * test complete
     */
    @Override
    public void testComplete(TestCaseEvent event) {
//        update finish time of test
        event.getFailureEvents().forEach(testStats::update);
    }

    public TestStats getTestStats() {
        return testStats;
    }
}
