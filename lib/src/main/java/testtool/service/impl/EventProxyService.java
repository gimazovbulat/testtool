package testtool.service.impl;

import testtool.QueueForEvents;
import testtool.TaskWithName;
import testtool.TestQueueListener;
import testtool.listeners.ResultsListener;
import testtool.util.PairOfListenerAndClass;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.identityHashCode;
import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * service that proxies listeners
 * */
public class EventProxyService {

    //    queue of events
    private final QueueForEvents queueForEvents;

    private final Map<PairOfListenerAndClass, Object> pairVsObject = new HashMap<>();

    //    hashcode method
    private static Method hashcode;
    //    equals method
    private static Method equals;

    static {
        try {
//            set equals method of object
            equals = Object.class.getMethod("equals", Object.class);
//            set hashcode method of object
            hashcode = Object.class.getMethod("hashCode");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    /**
     * add test queue listener
     *
     * @param testQueueListener - listener to add
     */
    public TestQueueListener addTestQueueListener(TestQueueListener testQueueListener) {
        return createProxy(testQueueListener, TestQueueListener.class);
    }

    /**
     * add result listener
     *
     * @param resultsListener - listener to add
     */
    public ResultsListener addResultListener(ResultsListener resultsListener) {
        return createProxy(resultsListener, ResultsListener.class);
    }

    /**
     * create proxy
     */
    private <T> T createProxy(T listener, Class<T> proxyI) {
//        create new pair of listener and prox interface
        PairOfListenerAndClass pairOfListenerAndClass = new PairOfListenerAndClass(proxyI, listener);
        //if map already contsins
        if (!pairVsObject.containsKey(pairOfListenerAndClass)) {
//            get classloader of class
            ClassLoader loader = getClass().getClassLoader();
            Class<?>[] interfaces = {proxyI};
//            create handler and put into map
            pairVsObject.put(pairOfListenerAndClass, newProxyInstance(loader, interfaces, createHandler(listener)));
        }
//        return proxy
        return (T) pairVsObject.get(pairOfListenerAndClass);
    }

    /**
     * create handler
     *
     * @param t - listener
     */
    private <T> InvocationHandler createHandler(final T t) {
//        return handler
        return (p, m, as) -> {
//            if method is hashcode
            if (m.equals(hashcode)) {
                return identityHashCode(p);
            } else if (m.equals(equals)) {
//                else if equsls
                return p == as[0];
            }

//            push event to event queue
            queueForEvents.addWithName(new TaskWithName("Handling") {
                @Override
                public void run() {
                    try {
//                        invoke method of listener with arguments
                        m.invoke(t, as);
                    } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            });
            return null;
        };
    }

    public EventProxyService(QueueForEvents queueForEvents) {
        this.queueForEvents = queueForEvents;
    }
}
