package testtool;

import testtool.data.BaseClassDto;
import testtool.data.RuntimeDto;
import testtool.data.TestCaseEvent;
import testtool.listeners.ListenersHandler;
import testtool.listeners.ResultListenerImpl;
import testtool.runner.queue.DefaultTestComparator;
import testtool.service.impl.EventProxyService;
import testtool.service.impl.FileChangeFinder;
import testtool.service.impl.ProcessHandler;
import testtool.service.impl.TestClassDetector;

import java.io.File;
import java.util.Collection;

import static testtool.util.MiscUtil.classesToNames;

/**
 *
 */
public class TestHandler {
    //  handler of listeners
    private final ListenersHandler listenersHandler;
    //    handler of test process
    private final ProcessHandler processHandler;
    //    detector of test classes
    private TestClassDetector testClassDetector;
    //    service that finds file changes
    private FileChangeFinder changeFinder;
    private RuntimeDto env;

    public TestHandler(ProcessHandler testRunner, QueueForEvents queueForEvents) {
        ResultListenerImpl listener = new ResultListenerImpl();
        processHandler = testRunner;
//        add listener
        processHandler.addResultListeners(listener);
//        set comparator to prioritise tests
        processHandler.setTestComparator(new DefaultTestComparator(listener));
//        create listener handler
        listenersHandler = new ListenersHandler(new EventProxyService(queueForEvents), processHandler);

    }

    //    private final Set<Class<? extends Throwable>> caughtExceptions = new LinkedHashSet<>();

    //    run tests
    public synchronized int run() {
        Collection<File> classes = changeFinder.findChangedFiles();
        int run = run(classes);
        return run;
    }

    /**
     * run tests on changed files
     */
    public synchronized int run(Collection<File> changedFiles) {
//        detect changes
        Collection<BaseClassDto> testsToRun = testClassDetector.getTests(changedFiles);
//        push tests to queue
        processHandler.pushToQueue(classesToNames(testsToRun));
//        return amount of tests
        return testsToRun.size();
    }

    /*
     * refresh tests
     * */
    public void refresh() {
        testClassDetector.clear();
        changeFinder.clearModifInfo();
        listenersHandler.getReloadListeners().forEach(SystemRefreshListener::refreshing);
    }


    /*
     * set new env
     * */
    public void setRuntimeEnvironment(RuntimeDto runtimeDto) {
//        if new
        if (!runtimeDto.equals(env)) {
//            set enviroment
            env = runtimeDto;
//            set enviroment to processhandler
            processHandler.setEnv(runtimeDto);
//            set enviroment to changefinder
            changeFinder.setRuntimeDto(runtimeDto);
//            set enviroment to class detector
            testClassDetector.setRuntimeDto(runtimeDto);
            refresh();
        }
    }

    /*
     * is source for events
     * */
    public boolean sourceForEvent(TestCaseEvent testCaseEvent) {
        return testCaseEvent.getSource().equals(this.getProcessHandler());
    }

    public ListenersHandler getListenersHandler() {
        return listenersHandler;
    }

    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    public TestClassDetector getTestClassDetector() {
        return testClassDetector;
    }

    public void setTestClassDetector(TestClassDetector testClassDetector) {
        this.testClassDetector = testClassDetector;
    }

    public FileChangeFinder getChangeFinder() {
        return changeFinder;
    }

    public void setChangeFinder(FileChangeFinder changeFinder) {
        this.changeFinder = changeFinder;
    }

    public RuntimeDto getEnv() {
        return env;
    }

    public void setEnv(RuntimeDto env) {
        this.env = env;
    }
}
