package testtool;

import ru.testtool.runner.TestEvent;

import java.util.Collection;

public interface TestFailureListener {

    void listUpdated(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved);

    void eventsUpdated(Collection<TestEvent> updatedFailures);
}