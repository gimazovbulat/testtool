package testtool.listeners;

import ru.testtool.runner.TestEvent;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class TestStats {

    private final Map<String, Long> finishTime = new HashMap<>();

    /**
     * get last finish time as milliseconds for test name
     */
    public long getLastFinishTime(String name) {
//        if there is test in map
        if (!finishTime.containsKey(name)) {
            return 0;
        }
//        return time
        return finishTime.get(name);
    }

    /**
     * update finish time of test
     * */
    protected void update(TestEvent event) {
        finishTime.put(event.getTestName(), currentTimeMillis());
    }

    public Map<String, Long> getFinishTime() {
        return finishTime;
    }
}
