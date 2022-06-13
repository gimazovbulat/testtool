package testtool.service.impl;

import testtool.data.RuntimeDto;
import testtool.data.filter.ClassFilter;
import testtool.util.Container;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static testtool.util.MiscUtil.isClass;

public class FileChangeFinder {

    private final Map<File, Long> fileVsLastChangeTIme = new HashMap<>();
    //    class directories
    private final List<File> classDirs = new ArrayList<>();
    private final RuntimeService runtimeService = Container.runtimeService;

    public void setRuntimeDto(RuntimeDto runtime) {
//        clear map
        fileVsLastChangeTIme.clear();
//        clear class directories
        this.classDirs.clear();
//        add class directories
        this.classDirs.addAll(runtimeService.classDirsInPath(runtime));
    }

    /**
     * find changed files
     */
    public synchronized List<File> findChangedFiles() {
        return getFiles(classDirs, false);
    }

    /**
     * find removed files
     */
    public List<File> getDeletedFiles() {
//        go through files
        return fileVsLastChangeTIme.keySet().stream()
//            if doesn't exist
            .filter(it -> !it.exists())
//            collect to list and return
            .collect(Collectors.toList());
    }

    /**
     * get files
     */
    private List<File> getFiles(
        List<File> classesOrDirs,
        boolean isDir
    ) {
//        result change files
        List<File> result = new ArrayList<>();
        for (File fileOrDir : classesOrDirs) {
//            if is directory
            if (isDir) {
//                get elements
                List<File> children = findElements(fileOrDir);
//                add to result list
                result.addAll(children);
//                if is class
            } else if (isClass(fileOrDir)) {
//                get last modification time
                Long changeTime = fileVsLastChangeTIme.get(fileOrDir);
//                if new time
                if ((Objects.isNull(changeTime)) || (fileOrDir.lastModified() != changeTime)) {
//                   save to map
                    fileVsLastChangeTIme.put(fileOrDir, fileOrDir.lastModified());
//                    add to list
                    result.add(fileOrDir);
                }
            }
        }
        return result;
    }

    public synchronized void clearModifInfo() {
        fileVsLastChangeTIme.clear();
    }

    /*
     * find elements in directory
     * */
    private List<File> findElements(File dit) {
//        get files from dir
        File[] elements = dit.listFiles(new ClassFilter());
        return Objects.isNull(elements) ?
            Collections.emptyList() :
            ///return files
            getFiles(List.of(elements), true);
    }
}
