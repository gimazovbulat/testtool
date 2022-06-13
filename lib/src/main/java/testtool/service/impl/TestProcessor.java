package testtool.service.impl;

import ru.testtool.runner.TestResults;
import testtool.data.RuntimeDto;

/**
 * service that runs test
 * */
public class TestProcessor {

//    service that sends events
    private final EventService eventService;
//    service that connects to process (socket)
    private final ConnectionServiceImpl connectionService;

    public TestProcessor(
        EventService eventService,
        ConnectionFactory factory,
        RuntimeDto environment
    ) {
        this.eventService = eventService;
        this.connectionService = factory.createConnection(environment);
    }

    /**
     * Запустить тест
     */
    public void run(String test) {
//        Запускает тесты и возвращает результаты
        TestResults results = connectionService.run(test);
//        Отправить оповещение, что тест завершился
        eventService.sendTestComplete(test, results);
    }

    /**
     * close socket
     * */
    public void close() {
//        eventService.fireTestRunComplete();
        connectionService.closeSocket();
    }

    /**
     * close connection
     * */
    public void closeConnection() {
        connectionService.close();
    }
}
