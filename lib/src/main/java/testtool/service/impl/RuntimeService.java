package testtool.service.impl;

import com.google.common.base.Charsets;
import ru.testtool.MyClassLoader;
import ru.testtool.runner.Main;
import testtool.data.RuntimeDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.io.File.pathSeparator;
import static java.io.File.separator;
import static java.io.File.separatorChar;
import static testtool.util.MiscUtil.validateFileName;

/**
 * Service to work with runtime
 */
public class RuntimeService {

    /**
     * create args for java process
     */
    public List<String> createArgs(
        RuntimeDto runtime,
        File classpathFile
    ) {
        List<String> args = new ArrayList<>();
        //add heap size
        args.add(runtime.getHeapSizeAsArg());
        //add java args to process args
        args.add(runtime.getJavaAsArg());
        //add classloader
        args.add("-Djava.system.class.loader=MyClassLoader");
        //add classpath file
        args.add("-Dru.testtool.service.classPathFile=" + classpathFile.getAbsolutePath());
        return args;
    }

    /*
     * create process env
     * */
    public Map<String, String> createProcessEnv(RuntimeDto runtimeDto) {
        Map<String, String> environment = new HashMap<>();
        //put classpath to env
        environment.put("CLASSPATH",
            findClasspathInPath(
                runtimeDto.getRunnerBootstrapClassPath(),
                MyClassLoader.class.getName()
            )
        );
        return environment;
    }

    /**
     * get classpath of runner
     */
    public String getRunnerPath(RuntimeDto runtime) {
        //getting path of runner jar
        String jarPath = findClasspathInPath(
            runtime.getRunnerProcClassPath(),
            Main.class.getName()
        );
        //return path for test runner with system separators
        return runtime.getTestProjectClasspath() + File.pathSeparator + jarPath;
    }

    /**
     * return class dirs in classpath as list
     */
    public List<File> classDirsInPath(RuntimeDto runtime) {
        //if there are no directories
        if (runtime.getClassDirs().isEmpty()) {
            List<File> classDirs = Stream.of(runtime.getTestProjectClasspath().split(pathSeparator))
                .map(File::new)
                //filtering only firectories
                .filter(File::isDirectory)
                //collecting to list
                .collect(Collectors.toList());

            //add directories to env
            runtime.getClassDirs().addAll(classDirs);
        }
        return runtime.getClassDirs();
    }

    /*
     * get runner classpath elems from runtime
     * */
    public List<String> getRunnerPathElements(RuntimeDto runtimeDto) {
        return newArrayList(getRunnerPath(runtimeDto).split(separator));
    }

    /**
     * create classpath from runtime
     */
    public File createClasspath(RuntimeDto runtimeDto) {
        try {
            //create temporary file with prefix and suffix
            File path = File.createTempFile("testtool-", ".classpath");
            path.deleteOnExit();
            //write elements to path
            Files.write(path.toPath(), getRunnerPathElements(runtimeDto), Charsets.UTF_8);
            return path;
        } catch (IOException e) {
            throw new IllegalStateException("Something went wrong while editing classpath file", e);
        }
    }

    public static String findClasspathInPath(String path, String className) {
        String classToLookFor = className.replace('.', '/');
        String[] classpath = path.split(pathSeparator);
        for (String filename : classpath) {
            File file = new File(filename);
//            if file is directory
            if (file.isDirectory()) {
                File clazzToFind = new File(file.getAbsolutePath() + separatorChar + classToLookFor);
                //and exists
                if (clazzToFind.exists()) {
                    //return name with normal separators
                    return validateFileName(filename);
                }
            } else {
                try (JarFile jarFile = new JarFile(filename)) {
                    if (Objects.nonNull(jarFile.getJarEntry(classToLookFor))) {
                        return validateFileName(filename);
                    }
                } catch (IOException ignored) {
                }
            }
        }
        return null;
    }
}
