package testtool;

import testtool.data.RuntimeDto;
import testtool.service.impl.FileChangeFinder;
import testtool.service.impl.ProcessHandler;
import testtool.service.impl.TestClassDetector;

import java.lang.reflect.InvocationTargetException;

import static com.google.common.base.Preconditions.checkNotNull;

public class TestToolCoreBuilder {
	private final Class<ProcessHandler> runnerClass;
	private final RuntimeDto runtimeEnvironment;
	private final QueueForEvents queueForEvents;
	private String coreName = "";

	public TestToolCoreBuilder(RuntimeDto environment, QueueForEvents queueForEvents) {
		checkNotNull(environment, "No runtime environment is configured. Maybe because the project has no jdk.");

		runtimeEnvironment = environment;
		this.queueForEvents = queueForEvents;
		
		runnerClass = ProcessHandler.class;
	}

	/**
	 * Creates a new core from the existing builder settings.
	 */
	public TestHandler createCore() {
		ProcessHandler runner = createRunner();
		TestHandler core = new TestHandler(runner, queueForEvents);
		core.setChangeFinder(new FileChangeFinder());
		core.setTestClassDetector(createTestDetector());
		core.setRuntimeEnvironment(runtimeEnvironment);
		return core;
	}

	protected TestClassDetector createTestDetector() {
		return new TestClassDetector();
	}

	private ProcessHandler createRunner() {
		try {
			return runnerClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Cannot create runner from class " + runnerClass + ". Did you provide a no-arg constructor?", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Cannot access runner class " + runnerClass, e);
		}
	}

	public void setName(String coreName) {
		this.coreName = coreName;
	}
}
