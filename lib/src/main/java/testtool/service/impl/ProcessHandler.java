package testtool.service.impl;

import ru.testtool.runner.RunnerImpl;
import testtool.TestQueueListener;
import testtool.data.RuntimeDto;
import testtool.listeners.ResultsListener;
import testtool.runner.queue.AbstractQueueService;
import testtool.runner.queue.NoComparator;
import testtool.runner.queue.TestQueue;

import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class ProcessHandler {

    //    queue service
    private final AbstractQueueService queueService;
    //    priority queue with tests
    private final Queue<String> queue;
    //    event service that sends different events
    private final EventService eventService;
    //    comparator to prioritise tests
    private Comparator<String> testComparator;
    //    env for process
    private RuntimeDto env;

    public ProcessHandler() {
        this(new ConnectionFactory(RunnerImpl.class), null);
    }

    public ProcessHandler(
        ConnectionFactory factory,
        RuntimeDto env
    ) {
//        queue with no comparator
        testComparator = new NoComparator();
//        queue for tests
        queue = new TestQueue(testComparator);
//        setting event service
        eventService = new EventService(this);
//        setting queue service
        queueService = new AbstractQueueService(queue, eventService) {
            @Override
            public TestProcessor creatProcessor() {
                return new TestProcessor(eventService, factory, env);
            }
        };
    }

    //    add result listener
    public void addResultListeners(ResultsListener listener) {
//        add listener to event service
        eventService.addResultListener(listener);
    }

    //    add queue listener
    public void addTestQueueListener(TestQueueListener listener) {
        //        add listener to event service
        eventService.addQueueListener(listener);
    }

    //    push tests to queue
    public void pushToQueue(List<String> testNames) {
        if (!testNames.isEmpty()) {
//            add tests to queue
            queueService.push(testNames);
        }
    }

    public AbstractQueueService getQueueService() {
        return queueService;
    }

    public Queue<String> getQueue() {
        return queue;
    }

    public EventService getEventService() {
        return eventService;
    }

    public Comparator<String> getTestComparator() {
        return testComparator;
    }

    public void setTestComparator(Comparator<String> testComparator) {
        this.testComparator = testComparator;
    }

    public RuntimeDto getEnv() {
        return env;
    }

    public void setEnv(RuntimeDto env) {
        this.env = env;
    }
}
