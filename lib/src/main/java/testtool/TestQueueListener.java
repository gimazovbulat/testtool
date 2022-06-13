package testtool;

public interface TestQueueListener extends SystemRefreshListener {

	void testQueueUpdated(TestQueueEvent event);

	void testRunComplete();
}
