package testtool;

public abstract class TaskWithName implements Runnable {
	private final String taskName;

	public TaskWithName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskName() {
		return taskName;
	}
}
