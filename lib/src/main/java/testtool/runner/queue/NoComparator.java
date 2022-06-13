package testtool.runner.queue;

import java.util.Comparator;

/**
 * comparator that doesn't do anything
 */
public class NoComparator implements Comparator<String> {

    /**
     *
     * "compare" tests
     * */
    @Override
    public int compare(String o1, String o2) {
        return 0;
    }
}
