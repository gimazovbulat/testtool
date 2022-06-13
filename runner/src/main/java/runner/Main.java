package runner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {
	public static final String TEST_RUN_ERROR = "Error occurred during test run";
	
	private RunnerImpl runner;

	private Main(String runnerClass) {
		createRunner(runnerClass);
	}

	private void createRunner(String runnerClassName) {
		runner = instantiateTestRunner(runnerClassName);
	}

	private RunnerImpl instantiateTestRunner(String runnerClassName) {
		try {
			Class<?> runnerClass = Class.forName(runnerClassName);
			return (RunnerImpl) runnerClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private TestResults runTest(String testName) {
		return runner.runTest(testName);
	}

	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				throw new IllegalArgumentException("runner expects two parameters: runnerClass and port");
			}
			String runnerClass = args[0];
			Main process = new Main(runnerClass);
			int portNum = Integer.parseInt(args[1]);
			Socket clientSocket = new Socket("127.0.0.1", portNum);
			// DEBT Extract this to a reader class
			ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

			String testName;
			do {
				testName = (String) inputStream.readObject();

				if (testName != null) {
					writeTestResultToOutputStream(process, outputStream, testName);
				}

			} while (testName != null);

			outputStream.close();
			clientSocket.close();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

	private static void writeTestResultToOutputStream(
		Main process,
		ObjectOutputStream outputStream,
		String testName
	) throws IOException {
		TestResults results;
		try {
			results = process.runTest(testName);
		}
		catch (Throwable e) {
			results = new TestResults(TestEvent.methodFailed(testName, "", e));
		}
		outputStream.writeObject(results);
	}
}
