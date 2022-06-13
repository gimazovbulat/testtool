package testtool.runner.queue;


import testtool.TestQueueEvent;
import testtool.service.impl.EventService;
import testtool.service.impl.TestProcessor;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;

public abstract class AbstractQueueService {
    //    queue with tests
    private final Queue<String> queue;
    private final EventService eventService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * push tests to queue
     */
    public void push(List<String> tests) {
//        add tests to queue
        queue.addAll(tests);
//        start tests
        handle();
    }

    /**
     * handle tests
     */
    private void handle() {
//        create runnable
        ProcessorRunnable runnable = new ProcessorRunnable(creatProcessor(), queue, eventService, queue.size());
        executor.execute(() -> {
//            start task
            runnable.run();
//            send event
            eventService.sendQueueEvent(new TestQueueEvent(newArrayList(queue), queue.size()));
        });
    }

    /*
     * abstract method to create processor
     * */
    public abstract TestProcessor creatProcessor();

    public AbstractQueueService(Queue<String> queue, EventService eventService) {
        this.queue = queue;
        this.eventService = eventService;
    }
}
