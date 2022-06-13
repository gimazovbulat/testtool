package testtool;

//queue that holds events
public interface QueueForEvents {

//	add task to queue
	void add(Runnable task);

	//	add task to queue
	void addWithName(TaskWithName task);
}
