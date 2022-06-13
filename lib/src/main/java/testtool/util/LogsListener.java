package testtool.util;

import java.util.logging.Level;

/**
 * listener for logs
 * */
public interface LogsListener {

//	log error
	void errorHappened(String msg, Throwable ex);

//	lof msg
	void logMessage(Level lvl, String msg);
}
