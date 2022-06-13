package testtool.runner.queue;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * test queue with tests
 * */
public class TestQueue extends PriorityBlockingQueue<String> {

	private static final long serialVersionUID = -1213415346533L;

	/**
	 * create queue with comparator and size
	 * */
	public TestQueue(Comparator<String> comparator) {
		super(10, comparator);
	}

	/**
	 * add test to queue
	 * */
	@Override
	public boolean add(String test) {
//		if not in queue then add
		if (!contains(test)) {
			return super.add(test);
		}
		return false;
	}
}
