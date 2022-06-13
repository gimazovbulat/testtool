package testtool.runner.queue;

import testtool.listeners.ResultListenerImpl;

import java.util.Comparator;

/**
 * default test comparator
 */
public class DefaultTestComparator implements Comparator<String> {

//    stats of tests
    private final ResultListenerImpl resultListener;

    /**
     * now comparing only last failure time
     */
    @Override
    public int compare(String test1, String test2) {
        //compare first test and second test
        return Long.compare(
            resultListener.getTestStats().getLastFinishTime(test2),
            resultListener.getTestStats().getLastFinishTime(test1)
        );
    }

    public DefaultTestComparator(ResultListenerImpl resultListener) {
        this.resultListener = resultListener;
    }
}
