package testtool.runner.queue;

import testtool.TestQueueEvent;
import testtool.service.impl.EventService;
import testtool.service.impl.TestProcessor;

import java.util.Queue;

import static com.google.common.collect.Lists.newArrayList;

/**
 * task which is representation of test run in queue
 * */
public class ProcessorRunnable implements Runnable {

//    test processor
    private final TestProcessor processor;
//    queue for tests
    private final Queue<String> queue;
//    service that works with events
    private final EventService eventService;
//    size of queue
    private final int size;

    @Override
    public void run() {
        synchronized (this) {
            String currentTest = null;
            try {
//                if queue not empty
                while (!queue.isEmpty()) {
//                    get test from queue
                    currentTest = queue.poll();
//                    run test
                    processor.run(currentTest);
//                    fire event
                    eventService.sendQueueEvent(
                        new TestQueueEvent(newArrayList(queue), size)
                    );
                }
            } catch (Exception e) {
//                if something happened requeue test
                if (currentTest != null) {
                    queue.add(currentTest);
                }
//                close connection
                processor.closeConnection();
            } finally {
//                close
                processor.close();
            }
        }
    }

    public ProcessorRunnable(TestProcessor processor, Queue<String> queue, EventService eventService, int size) {
        this.processor = processor;
        this.queue = queue;
        this.eventService = eventService;
        this.size = size;
    }
}