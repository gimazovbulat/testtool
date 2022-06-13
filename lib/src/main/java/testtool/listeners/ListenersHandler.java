package testtool.listeners;

import testtool.SystemRefreshListener;
import testtool.TestQueueListener;
import testtool.service.impl.EventProxyService;
import testtool.service.impl.ProcessHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * service that works with event listners
 * */
public class ListenersHandler {

    //    service that proxies listeners
    private final EventProxyService eventProxyService;
    //    service that handles test process
    private final ProcessHandler processHandler;
    //    list of reload listeners
    private final List<SystemRefreshListener> systemRefreshListeners = new ArrayList<>();
    private final Set<Class<? extends Throwable>> caughtExceptions = new LinkedHashSet<>();

    //    add result listener
    public void addTestResultsListener(ResultsListener listener) {
        //add proxy of result listener
        processHandler.addResultListeners(eventProxyService.addResultListener(listener));
    }

    //    add test queue listener
    public void addTestQueueListener(TestQueueListener listener) {
//        add proxy of listener
        processHandler.addTestQueueListener(eventProxyService.addTestQueueListener(listener));
//        add to reload listener list
        systemRefreshListeners.add(listener);
    }

    public EventProxyService getEventProxyService() {
        return eventProxyService;
    }

    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    public List<SystemRefreshListener> getReloadListeners() {
        return systemRefreshListeners;
    }

    public Set<Class<? extends Throwable>> getCaughtExceptions() {
        return caughtExceptions;
    }

    public ListenersHandler(EventProxyService eventProxyService, ProcessHandler processHandler) {
        this.eventProxyService = eventProxyService;
        this.processHandler = processHandler;
    }
}
