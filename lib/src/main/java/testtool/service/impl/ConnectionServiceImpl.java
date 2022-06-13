package testtool.service.impl;

import ru.testtool.runner.TestResults;

import java.io.File;

/**
 * connection service that golds connection
 * */
public class ConnectionServiceImpl {

//    tcp socket handler
    private final TcpSocketHandler socketHandler;
//    process with test
    private final Process process;
//    classpsth file
    private final File classpath;

    public ConnectionServiceImpl(TcpSocketHandler socketHandler, Process process, File classpath) {
        this.socketHandler = socketHandler;
        this.process = process;
        this.classpath = classpath;
    }

    /**
     * delete process
     * */
    public void close() {
        try {
            classpath.delete();
            process.destroy();
            process.waitFor();
        } catch (Exception e) {
        }
    }

    /**
     * close socket
     * */
    public void closeSocket() {
        socketHandler.close();
    }

    /**
     * run tests
     *
     * @param name - test on name
     * */
    public TestResults run(String name) {
        return socketHandler.writeTestNameToSocket(name);
    }
}
