package testtool.data;

/**
 * Возможные состояния системы
 */
public enum Status {

    /**
     * Начальное состояние, строим граф классов и т.д.
     */
    START,
    /**
     * Ищем тесты, если тестов нет - остаемся в этом статусе
     */
    SEARCHING,

    /**
     * Прогоняются тесты, если этот статус активен - очередь с тестами не пуста
     */
    EXECUTING,

    /**
     * По крайней мере один тест прошел
     */
    AT_LEAST_ONE_PASSED,

    /**
     * По крайней мере один тест завершился с ошибкой
     */
    FAILED
}
