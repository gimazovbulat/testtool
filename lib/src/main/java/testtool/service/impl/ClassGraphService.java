package testtool.service.impl;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import testtool.data.BaseClassDto;
import testtool.data.RuntimeDto;
import testtool.util.Container;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jgrapht.Graphs.predecessorListOf;

/**
 * Service that works with graph
 */
public class ClassGraphService {
    //    service that works with classes (changed)
    private final ClassService classService;
    //    directed graph with classes as vertexes
    private DefaultDirectedGraph<BaseClassDto, DefaultEdge> classGraph;

    public ClassGraphService(RuntimeDto runtime) {
        this.classService = new ClassService(Container.runtimeService.getRunnerPath(runtime));
//       create class graph
        classGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    /**
     * delete graph
     */
    public void deleteGraph() {
//        just assign new reference
        classGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    /**
     * find changed classes and update references
     */
    public Set<BaseClassDto> findChanged(Collection<File> changed) {
//        go through changed classes
        Set<String> changedClassNames = changed.stream()
            .map(classService::classFileChanged)
//            find only not null
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
//        update references between classes
        return updateRefs(changedClassNames);
    }

    /**
     * find class in pool or graph
     *
     * @param name - class name
     * @return found class
     */
    public BaseClassDto findInGraphOrPool(String name) {
//        find in grpah
        BaseClassDto clazz = findInGraphByName(name);
//        if null
        if (Objects.isNull(clazz)) {
//            find class by name
            clazz = classService.getClassByName(name);
            if (clazz.locatedInClassFile()) {
//                add to graph
                addToGraph(clazz);
//                update refenreces between classes
                updateParentRefs(clazz);
            }
        }
        return clazz;
    }

    //    add class to graph
    private void addToGraph(BaseClassDto clazz) {
        if (!classGraph.addVertex(clazz)) {
            replace(clazz);
        }
    }

    /*
     * find predecessors of clazz
     *
     * @param child - class of which to find parents
     *
     * */
    private List<BaseClassDto> predecessors(BaseClassDto child) {
        return predecessorListOf(classGraph, child);
    }

    /**
     * replace class in graph
     *
     * @param clazz - class to replace
     */
    private void replace(BaseClassDto clazz) {
//        find parents of class
        List<BaseClassDto> relations = predecessors(clazz);
//        remove class in graph
        classGraph.removeVertex(clazz);
//        add class to graph
        classGraph.addVertex(clazz);
//        add refenreces
        relations.forEach(cl -> classGraph.addEdge(cl, clazz));
    }

    /**
     * updare refences with class
     */
    private void updateParentRefs(BaseClassDto clazz) {
//        go through dependencies
        clazz.getDependencies().stream()
//            find in pool or grpah
            .map(this::findInGraphOrPool)
//            if fits
            .filter(child -> (Objects.nonNull(child)) && !child.equals(clazz))
//            if graph contains
            .filter(cl -> classGraph.containsVertex(cl))
            .forEach(cl -> classGraph.addEdge(clazz, cl));
    }

    /*-
     * find changed parents of classes
     * */
    public Set<BaseClassDto> findChangedPredecessors(Set<BaseClassDto> classes) {
        Set<BaseClassDto> changedParents = new HashSet<>(classes);
//        go through classes and find parents
        classes.forEach(cl -> predecessors(cl, changedParents));
        return changedParents;
    }

    /**
     * recursive
     * find parents of files
     *
     * @param clazz
     * @param changedParents - parents of class
     */
    private void predecessors(BaseClassDto clazz, Set<BaseClassDto> changedParents) {
        predecessors(clazz).stream()
//            filter
            .filter(changedParents::add)
//            find parents for each
            .forEach(cl -> predecessors(cl, changedParents));
    }

    public Set<String> getIndexedClasses() {
        Set<BaseClassDto> vertexSet = classGraph.vertexSet();
        return vertexSet.stream().map(BaseClassDto::getName).collect(Collectors.toSet());
    }

    /**
     * update refenreces of classes
     *
     * */
    private Set<BaseClassDto> updateRefs(Set<String> changed) {
        Set<BaseClassDto> changedClasses = new HashSet<>();
//        go through changed classes
        changed.stream()
//            map to class by name
            .map(classService::getClassByName)
//            only not null
            .filter(Objects::nonNull)
//            for each
            .forEach(it -> {
//                add to graph
                    addToGraph(it);
//                    update refs
                    updateParentRefs(it);
//                    add to changed
                    changedClasses.add(it);
                }
            );
        return changedClasses;
    }

    /**
     * find in graph by name
     * */
    private BaseClassDto findInGraphByName(String name) {
//        go through classes in graph
        for (BaseClassDto clazz : classGraph.vertexSet()) {
//            if class found
            if (clazz.getName().equals(name)) {
//                return
                return clazz;
            }
        }
        return null;
    }
}
