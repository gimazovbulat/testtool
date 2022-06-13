package testtool.data;

import ru.testtool.runner.TestEvent;
import ru.testtool.runner.TestResults;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

public class TestCaseEvent {
//	events about tests
	private final List<TestEvent> events;
	private final Object source;
	private final String name;
	private final TestResults results;

	public TestCaseEvent(String testName, Object source, TestResults results) {
		this.name = testName;
		this.source = source;
		this.results = results;
		events = newArrayList();
		for (TestEvent testEvent : results) {
			if (!isCompilationErrors(testEvent)) {
				events.add(testEvent);
			}
		}
	}

	private boolean isCompilationErrors(TestEvent testEvent) {
		return Objects.equals(testEvent.getFullErrorClassName(), VerifyError.class.getName())
				|| isUnresolvedCompilationProblemsError(testEvent);
	}

	private boolean isUnresolvedCompilationProblemsError(TestEvent testEvent) {
		return Objects.equals(testEvent.getFullErrorClassName(), Error.class.getName())
				&& testEvent.getMessage() != null
				&& testEvent.getMessage().contains("Unresolved compilation problems:");
	}

	public List<TestEvent> getFailureEvents() {
		return unmodifiableList(events);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof TestCaseEvent && name.equals(((TestCaseEvent) obj).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public List<TestEvent> getEvents() {
		return events;
	}

	public Object getSource() {
		return source;
	}

	public String getName() {
		return name;
	}

	public TestResults getResults() {
		return results;
	}
}
