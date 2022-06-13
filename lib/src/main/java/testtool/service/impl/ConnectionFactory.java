package testtool.service.impl;

import ru.testtool.runner.Main;
import ru.testtool.runner.RunnerImpl;
import testtool.data.RuntimeDto;
import testtool.util.Container;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

/*
 * factory that creates tcp connection
 *
 * */
public class ConnectionFactory {

    /*
     * class of runner
     * */
    private final Class<RunnerImpl> runner;
    /*
     * service to work with runtime
     * */
    private final RuntimeService runtimeService = Container.runtimeService;

    public ConnectionFactory(Class<RunnerImpl> testRunnerClass) {
        runner = testRunnerClass;
    }

    /**
     * create connection with given env
     *
     * @param env - runtime enviroment
     */
    public ConnectionServiceImpl createConnection(RuntimeDto env) {
//       service that holds tcp socket
        TcpSocketHandler tcpSocketHandler = new TcpSocketHandler();

//        create class path with given env
        File runtimeServiceClasspath = runtimeService.createClasspath(env);
//        start process
        Process process = startProcess(
            tcpSocketHandler.create(),
            env,
            runtimeServiceClasspath
        );
//        open socket
        tcpSocketHandler.open();
//        return representation of connection
        return new ConnectionServiceImpl(
            tcpSocketHandler,
            process,
            runtimeServiceClasspath
        );
    }

    /**
     * start process
     *
     * @param port      on which start process
     * @param env       for process
     * @param classpath classpath
     */
    private Process startProcess(
        int port,
        RuntimeDto env,
        File classpath
    ) {
//        build process with given args
        ProcessBuilder builder = buildProcess(port, env, classpath);
        try {
//            start process
            return builder.start();
        } catch (IOException e) {
//            if something went wrong throw ex
            throw new IllegalStateException("Something went wrong with process: " + buildLogMsg(builder), e);
        }
    }

    /**
     * build process with given args
     *
     * @param port      on which start process
     * @param env       for process
     * @param classpath classpath
     */
    private ProcessBuilder buildProcess(
        int port,
        RuntimeDto env,
        File classpath
    ) {
        ProcessBuilder builder = new ProcessBuilder();
//        set directory
        builder.directory(env.getWorkingDir());
//        arguments for process
        List<String> arguments = runtimeService.createArgs(env, classpath);
//        add port to args
        arguments.addAll(buildArgs(port));
//        set args as command
        builder.command(arguments);

        builder.environment().putAll(runtimeService.createProcessEnv(env));

        return builder;
    }


    /**
     * build args with port
     */
    private Collection<String> buildArgs(int port) {
        return asList(
            Main.class.getName(),
            runner.getName(),
            String.valueOf(port)
        );
    }

    /**
     * build msg for log
     */
    private String buildLogMsg(ProcessBuilder processBuilder) {
//        line separator
        String lineSeparator = System.getProperty("line.separator");

        StringBuilder stringBuilder = new StringBuilder();
//        add directory to log
        stringBuilder.append("  Dir: ").append(processBuilder.directory().getAbsolutePath()).append(lineSeparator);
//        add env args
        stringBuilder.append("  Env:").append(lineSeparator);
        processBuilder.environment().forEach((key, value) -> stringBuilder.append("    ").append(key).append("=").append(value).append(lineSeparator));

//        add command to log
        stringBuilder.append("  Command: ").append(processBuilder.command());

        return stringBuilder.toString();
    }
}
