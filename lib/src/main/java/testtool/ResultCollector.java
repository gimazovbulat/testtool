package testtool;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import ru.testtool.runner.PointOfFailure;
import ru.testtool.runner.TestEvent;
import testtool.data.Status;
import testtool.data.TestCaseEvent;
import testtool.listeners.ResultsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;

public class ResultCollector implements TestQueueListener, ResultsListener {

	private Status status;
	private final Map<String, TestCaseEvent> resultMap;
	private final List<TestFailureListener> changeListeners;
	private final List<SystemChangedListener> systemChangedListeners;
	private final ListMultimap<PointOfFailure, TestEvent> failuresByPointOfFailure;
	private final QueueService queueService;

	public ResultCollector() {
		resultMap = newHashMap();
		changeListeners = newArrayList();
		systemChangedListeners = newArrayList();
		failuresByPointOfFailure = ArrayListMultimap.create();
		status = Status.SEARCHING;
		queueService = new QueueService();
		queueService.addListener(this);
	}

	public ResultCollector(TestHandler core) {
		this();
		attachCore(core);
	}

	public void attachCore(TestHandler core) {
		core.getListenersHandler().addTestResultsListener(this);
		queueService.linkCore(core);
	}

	private List<String> findFailingTestsForCore(TestHandler core) {
		List<String> tests = newArrayList();
		for (TestCaseEvent eachEvent : resultMap.values()) {
			if (core.sourceForEvent(eachEvent)) {
				tests.add(eachEvent.getName());
			}
		}
		return tests;
	}

	public void addStatusChangeListener(SystemChangedListener listener) {
		systemChangedListeners.add(listener);
	}

	@Override
	public void testComplete(TestCaseEvent event) {
		TestCaseFailures failureSet = getCurrentFailuresForTestCase(event);
		for (TestEvent each : event.getFailureEvents()) {
			failuresByPointOfFailure.put(each.getPointOfFailure(), each);
			failureSet.addNewFailure(each);
		}
		resultMap.put(event.getName(), event);
		fireCachedFailureEvents(failureSet);
	}

	private TestCaseFailures getCurrentFailuresForTestCase(TestCaseEvent event) {
		TestCaseEvent oldEvent = resultMap.get(event.getName());
		if (oldEvent != null) {
			return new TestCaseFailures(oldEvent.getFailureEvents());
		}
		return new TestCaseFailures(noEvents());
	}

	private void fireCachedFailureEvents(TestCaseFailures testCaseFailures) {
		fireChangeEvent(testCaseFailures.newFailures(), testCaseFailures.removedFailures());
		fireUpdateEvent(testCaseFailures.updatedFailures());
	}

	public List<PointOfFailure> getPointsOfFailure() {
		List<PointOfFailure> resultList = newArrayList();
		for (TestEvent failure : getFailures()) {
			PointOfFailure pointOfFailure = failure.getPointOfFailure();
			if (!resultList.contains(pointOfFailure)) {
				resultList.add(pointOfFailure);
			}
		}
		return resultList;
	}

	public PointOfFailure getPointOfFailure(int i) {
		return getPointsOfFailure().get(i);
	}

	public boolean isPointOfFailure(Object parent) {
		return getPointsOfFailure().contains(parent);
	}

	public List<TestEvent> getTestsFor(PointOfFailure pointOfFailure) {
		List<TestEvent> matchedEvents = new ArrayList<TestEvent>();
		for (TestEvent event : getFailures()) {
			if (event.getPointOfFailure().equals(pointOfFailure)) {
				matchedEvents.add(event);
			}
		}
		return matchedEvents;
	}

	public int getPointOfFailureCount() {
		return getPointsOfFailure().size();
	}

	public int getPointOfFailureIndex(PointOfFailure pointOfFailure) {
		return getPointsOfFailure().indexOf(pointOfFailure);
	}

	public void addChangeListener(TestFailureListener listener) {
		changeListeners.add(listener);
	}

	public boolean hasFailures() {
		return !getFailures().isEmpty();
	}

	public List<TestEvent> getFailures() {
		List<TestEvent> failures = newArrayList();
		for (TestCaseEvent each : resultMap.values()) {
			failures.addAll(each.getFailureEvents());
		}
		return failures;
	}

	public void clear() {
		resultMap.clear();
		failuresByPointOfFailure.clear();
	}

	public List<TestEvent> getFailuresForPointOfFailure(PointOfFailure pointOfFailure) {
		return failuresByPointOfFailure.get(pointOfFailure);
	}

	public Status getStatus() {
		return status;
	}

	public void addTestQueueListener(TestQueueListener testQueueAdapter) {
		queueService.addListener(testQueueAdapter);
	}

	public void removeTestQueueListener(TestQueueListener listener) {
		queueService.removeListener(listener);
	}

	@Override
	public void testRunComplete() {
		if (hasFailures()) {
			setStatus(Status.FAILED);
		} else {
			setStatus(Status.AT_LEAST_ONE_PASSED);
		}
	}

	@Override
	public void testQueueUpdated(TestQueueEvent event) {
		if (!event.getTestQueue().isEmpty()) {
			setStatus(Status.EXECUTING);
		}
	}

	@Override
	public void refreshing() {
		List<TestEvent> failuresRemoved = newArrayList(getFailures());
		clear();
		setStatus(Status.SEARCHING);
		fireChangeEvent(noEvents(), failuresRemoved);
	}

	private List<TestEvent> noEvents() {
		return emptyList();
	}

	private void setStatus(Status newStatus) {
		Status oldStatus = status;
		status = newStatus;
		fireStatusChanged(oldStatus, newStatus);
	}

	private void fireStatusChanged(Status oldStatus, Status newStatus) {
		for (SystemChangedListener each : systemChangedListeners) {
			each.systemChanged(oldStatus, newStatus);
		}
	}

	private void fireChangeEvent(Collection<TestEvent> failuresAdded, Collection<TestEvent> failuresRemoved) {
		for (TestFailureListener each : changeListeners) {
			each.listUpdated(failuresAdded, failuresRemoved);
		}
	}

	private void fireUpdateEvent(Collection<TestEvent> updatedFailures) {
		for (TestFailureListener each : changeListeners) {
			each.eventsUpdated(updatedFailures);
		}
	}
}
