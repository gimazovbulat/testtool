package testtool.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;

/**
 * Representation of runtime
 */
public class RuntimeDto {

    //java home
    private final File javaHome;
    //working directory
    private final File workingDir;
    //classpath of test project
    private final String testProjectClasspath;
    //runner process classpath
    private final String runnerProcClassPath;
    //runner bootstrap classpath
    private final String runnerBootstrapClassPath;
    //class directories
    private final List<File> classDirs = new ArrayList<>();
    //output directories
    private final List<File> outputDirs;
    //heap size for process
    private int heapSize = 512;

    /**
     * memory settings as command args
     * */
    public String getHeapSizeAsArg() {
        return "-mx" + getHeapSize() + "m";
    }

    /**
     * java exec as args
     * */
    public String getJavaAsArg() {
        return new File(javaHome.getAbsolutePath().concat(separator)
            .concat("bin").concat(separator)
            .concat("java.exe")
        ).getAbsolutePath();
    }

    public RuntimeDto(File javaHome,
                      File workingDir,
                      String runnerBootstrapClassPath,
                      String runnerProcClassPath,
                      List<File> outputDirs,
                      String testProjectClasspath
                      ) {
        this.javaHome = javaHome;
        this.workingDir = workingDir;
        this.testProjectClasspath = testProjectClasspath;
        this.runnerProcClassPath = runnerProcClassPath;
        this.runnerBootstrapClassPath = runnerBootstrapClassPath;
        this.outputDirs = outputDirs;
    }

    public File getJavaHome() {
        return javaHome;
    }

    public File getWorkingDir() {
        return workingDir;
    }

    public String getTestProjectClasspath() {
        return testProjectClasspath;
    }

    public String getRunnerProcClassPath() {
        return runnerProcClassPath;
    }

    public String getRunnerBootstrapClassPath() {
        return runnerBootstrapClassPath;
    }

    public List<File> getClassDirs() {
        return classDirs;
    }

    public List<File> getOutputDirs() {
        return outputDirs;
    }

    public int getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
    }

    @Override
    public String toString() {
        return "RuntimeDto{" +
            "javaHome=" + javaHome +
            ", workingDir=" + workingDir +
            ", testProjectClasspath='" + testProjectClasspath + '\'' +
            ", runnerProcClassPath='" + runnerProcClassPath + '\'' +
            ", runnerBootstrapClassPath='" + runnerBootstrapClassPath + '\'' +
            ", classDirs=" + classDirs +
            ", outputDirs=" + outputDirs +
            ", heapSize=" + heapSize +
            '}';
    }
}
