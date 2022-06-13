package testtool.service.impl;

import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.io.File.pathSeparator;

/*
 * class that holds class pool
 * */
public class ClassPoolSingleton {

    //    classpool holds info about classes
    private ClassPool classPool;
    //    classpool
    private final String classpath;

    public ClassPoolSingleton(String classpath) {
        this.classpath = classpath;
    }

    /**
     * get class pool
     */
    public ClassPool getClassPool() {
        //if is null
        if (Objects.isNull(classPool)) {
            //create new one
            classPool = new ClassPool(true);
            getPathElements().forEach(it -> {
                try {
                    classPool.appendClassPath(it);
                } catch (NotFoundException e) {
                    classPool = null;
                    throw new IllegalStateException("Something went wrong, couldn't create pool", e);
                }
            });
        }
        return classPool;
    }

    /**
     * return elements from classpath
     */
    private List<String> getPathElements() {
//        get elements from classpath
        List<String> elems = new ArrayList<>(List.of(classpath.split(pathSeparator)));

//        go  through elems
        List<String> toDelete = elems.stream()
            //if not exists
            .filter(fileName -> !new File(fileName).exists())
            //add
            .collect(Collectors.toList());
///remove not existent
        elems.removeAll(toDelete);
        return elems;
    }

    public String getClasspath() {
        return classpath;
    }
}
