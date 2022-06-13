package runner;

import java.io.Serializable;

public class TestCaseStats implements Serializable {

	public long startTime;
	public long stopTime;
	public final String methodName;

	public TestCaseStats(String methodName) {
		this.methodName = methodName;
	}

	private static final long serialVersionUID = -7744786534448875523L;
}
