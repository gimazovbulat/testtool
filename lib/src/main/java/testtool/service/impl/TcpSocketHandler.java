package testtool.service.impl;

import ru.testtool.runner.TestResults;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * service that works with tcp socket
 */
public class TcpSocketHandler {

    //	server socket
    private ServerSocket serverSocket;
    //	read results from
    private ObjectInputStream in;
    //	write info to
    private ObjectOutputStream out;
    private Socket socket;

    /**
     * create socket
     */
    public int create() {
//		if not null and server socket is open
        if ((Objects.nonNull(serverSocket)) && !serverSocket.isClosed()) {
//			throw exception
            throw new IllegalStateException("Socket is already open");
        }
        try {
//			start socket on some port
            serverSocket = new ServerSocket(0);
//			set timeout
            serverSocket.setSoTimeout(1500);
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong", e);
        }
//		return port of server socket
        return serverSocket.getLocalPort();
    }

    /**
     * open server socket
     */
    public void open() {
        try {
//			open socket
            socket = serverSocket.accept();
//			set inputstream
            in = new ObjectInputStream(socket.getInputStream());
//			set outputstream
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * close socket
     */
    public synchronized void close() {
        try {
            if (out != null) {
                out.writeObject(null);
                if (!socket.isClosed()) {
                    //close verything
                    //inputstream
                    in.close();
                    in = null;
                    //outputstream
                    out.close();
                    out = null;
                    //socket
                    socket.close();
                    socket = null;
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * write test name to socket
     *
     * @param testName name of socket
     */
    public synchronized TestResults writeTestNameToSocket(String testName) {
        try {
//			write test name to socket
            out.writeObject(testName);
            //read results
            return (TestResults) in.readObject();
        } catch (Exception e) {
            throw new IllegalStateException("something went wrong with test " + testName, e);
        }
    }
}
