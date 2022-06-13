package testtool;

import ru.testtool.runner.TestEvent;

import java.util.Arrays;

public class TestEventEqualityAdapter {
	private final TestEvent event;

	public TestEventEqualityAdapter(TestEvent event) {
		this.event = event;
	}

	public TestEvent getEvent() {
		return event;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestEventEqualityAdapter) {
			TestEventEqualityAdapter other = (TestEventEqualityAdapter) obj;
			return com.google.common.base.Objects.equal(event, other.event) && com.google.common.base.Objects.equal(event.getMessage(), other.event.getMessage()) && com.google.common.base.Objects.equal(event.getPointOfFailure(), other.event.getPointOfFailure()) && Arrays.equals(event.getStackTrace(), other.event.getStackTrace());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return com.google.common.base.Objects.hashCode(event) ^ com.google.common.base.Objects.hashCode(event.getMessage()) ^ com.google.common.base.Objects.hashCode(event.getPointOfFailure()) ^ Arrays.hashCode(event.getStackTrace());
	}
}