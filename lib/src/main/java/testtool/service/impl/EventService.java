package testtool.service.impl;

import ru.testtool.runner.TestResults;
import testtool.TestQueueEvent;
import testtool.TestQueueListener;
import testtool.data.TestCaseEvent;
import testtool.listeners.ResultsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that sends events
 */
public class EventService {

//    queue listeners
    private final List<TestQueueListener> queueListeners = new ArrayList<>();
//    test result listeners
    private final List<ResultsListener> resultsListeners = new ArrayList<>();
    private final Object source;

    public EventService(Object eventSource) {
        source = eventSource;
    }

    /**
     * send test complete event with results
     * */
    public void sendTestComplete(String testName, TestResults results) {
        resultsListeners.forEach(it -> it.testComplete(new TestCaseEvent(testName, source, results)));
    }

    /**
     * send event
     */
    public void sendQueueEvent(TestQueueEvent event) {
        queueListeners.forEach(it -> it.testQueueUpdated(event));
    }

    /**
     * add listener to list
     */
    public void addResultListener(ResultsListener listener) {
        resultsListeners.add(listener);
    }

    /**
     * add listener to list
     */
    public void addQueueListener(TestQueueListener listener) {
        queueListeners.add(listener);
    }

//    public void fireTestRunComplete() {
//        for (TestQueueListener each : testQueueListenerList) {
//            each.testRunComplete();
//        }
//    }

}
