package testtool.listeners;


import testtool.data.TestCaseEvent;

public interface ResultsListener {

	void testComplete(TestCaseEvent event);
}
