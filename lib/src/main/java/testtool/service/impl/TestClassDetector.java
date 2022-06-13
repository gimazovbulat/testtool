package testtool.service.impl;

import testtool.data.BaseClassDto;
import testtool.data.RuntimeDto;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * service to find classes
 */
public class TestClassDetector {

    //    service with graph
    private ClassGraphService classGraphService;

    //    get tests from changed classes
    public synchronized Set<BaseClassDto> getTests(Collection<File> changedClasses) {
        Set<BaseClassDto> changedClassesAndTests = new HashSet<>();
//      find changed classes
        Set<BaseClassDto> changed = classGraphService.findChanged(changedClasses);
        //add to result set
        changedClassesAndTests.addAll(changed);
        //find parents of changed classes
        changedClassesAndTests.addAll(classGraphService.findChangedPredecessors(changed));

        //get only tests
        return findOnlyTests(changedClassesAndTests);
    }

    //    set runtime
    public void setRuntimeDto(RuntimeDto classpath) {
        classGraphService = new ClassGraphService(classpath);
    }

    //    filter only tests
    private Set<BaseClassDto> findOnlyTests(Set<BaseClassDto> changedClasses) {
        return changedClasses.stream()
            .filter(BaseClassDto::isATest)
            .collect(Collectors.toSet());
    }

    //    get current tests
    public Set<String> getCurrentTests() {
        return classGraphService.getIndexedClasses().stream()
            //find class in pool or graph
            .filter(cl -> classGraphService.findInGraphOrPool(cl).isATest())
            .collect(Collectors.toSet());
    }

    //delete graph
    public void clear() {
        classGraphService.deleteGraph();
    }

    public TestClassDetector() {
    }
}
