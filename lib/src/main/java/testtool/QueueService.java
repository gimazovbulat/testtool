package testtool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.max;

/**
 * another service that works with queues
 */
public class QueueService {
//    initial size of queue
    private int size = 0;
//    handler vs listeners
    private final Map<TestHandler, AnotherQueueListener> listenerMap = new LinkedHashMap<>();
//    list of test queue listeners
    private final List<TestQueueListener> listenersList = new ArrayList<>();

//    attach core
    public void linkCore(TestHandler testHandler) {
//        create new listener
        AnotherQueueListener listener = new AnotherQueueListener();
//        put core vs listener
        listenerMap.put(testHandler, listener);
//        add listener to list
        testHandler.getListenersHandler().addTestQueueListener(listener);
    }

    private void sendQueueEvent() {
//        get queue
        List<String> queue = getQueue();
//        set size as mac
        size = max(size, queue.size());
        listenersList.forEach(it -> it.testQueueUpdated(new TestQueueEvent(queue, size)));
//      if queue isn't empty then size is not 0
        size = queue.isEmpty() ? 0 : size;
    }

    /**
     * return queue of all listeners
     * */
    private List<String> getQueue() {
        return listenerMap.values().stream()
            .map(it -> it.queue)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private void sendRefreshEvent() {
        listenersList.forEach(SystemRefreshListener::refreshing);
    }

    private void sendCompleteEvent() {
        if (getQueue().isEmpty()) {
//            if not empty yhen send event of complete wun
            listenersList.forEach(TestQueueListener::testRunComplete);
        }
    }

    Map<TestHandler, AnotherQueueListener> getListenerMap() {
        return listenerMap;
    }

//    add listener
    public void addListener(TestQueueListener testQueueAdapter) {
        listenersList.add(testQueueAdapter);
    }

//    remove listener
    public void removeListener(TestQueueListener listener) {
        listenersList.remove(listener);
    }

    private class AnotherQueueListener extends TestQueueAdapter {

        private List<String> queue = new ArrayList<>();

        @Override
        public void refreshing() {
            sendRefreshEvent();
        }

        @Override
        public void testRunComplete() {
            sendCompleteEvent();
        }

        @Override
        public void testQueueUpdated(TestQueueEvent event) {
            queue = event.getTestQueue();
            sendQueueEvent();
        }
    }
}
