package runner.junit5;

import org.junit.platform.engine.Filter;
import org.junit.platform.launcher.EngineFilter;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import runner.TestResults;

import java.util.ArrayList;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class Runner {

    public TestResults runTest(Class<?> clazz) {
        List<Filter<?>> filters = new ArrayList<>();
        filters.add(EngineFilter.includeEngines("junit-jupiter"));
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(clazz))
            .filters(filters.toArray(new Filter[0]))
            .build();

        Launcher launcher = LauncherFactory.create();

        TestExecutionListenerImpl listener = new TestExecutionListenerImpl();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        return listener.getTestResults();
    }
}
