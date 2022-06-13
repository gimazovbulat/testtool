package runner;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

public class TestResults implements Iterable<TestEvent>, Serializable {

	private final List<TestEvent> eventsCollected;
	private final List<TestCaseStats> testCaseStats = new LinkedList<TestCaseStats>();

	public TestResults(List<TestEvent> eventsCollected) {
		this.eventsCollected = eventsCollected;
	}

	public TestResults(TestEvent... failures) {
		this(asList(failures));
	}

	@Override
	public Iterator<TestEvent> iterator() {
		return eventsCollected.iterator();
	}

	private static final long serialVersionUID = 965321348614354650L;

	public Iterable<TestCaseStats> getMethodStats() {
		return testCaseStats;
	}

	public void addMethodStats(Collection<TestCaseStats> methodStatistics) {
		testCaseStats.addAll(methodStatistics);
	}
}
