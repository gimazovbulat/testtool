package testtool;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;

/**
 * Custom class loader
 */
public class MyClassLoader extends URLClassLoader {

    public MyClassLoader(ClassLoader loader) {
        this(loader, readElemsFromClassPath());
    }

    public MyClassLoader(ClassLoader parentLoader, List<String> classPathEntries) {
        super(toUrlEntries(classPathEntries), parentLoader);
    }

    private static List<String> readElemsFromClassPath() {
//        get classpathfile
        String classPathFile = System.getProperty("ru.testtool.classloader.classPathFile");
        try {
//            read all entries in classpath
            return Files.readAllLines(new File(classPathFile).toPath());
        } catch (IOException e) {
            throw new IllegalArgumentException("Something is wrong with classpath: " + classPathFile);
        }
    }


    /**
     *
     */
    private static URL[] toUrlEntries(List<String> elems) {
        return elems.stream().map(File::new).map(it -> {
            try {
                return it.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Something is wrong " + it, e);
            }
        }).toArray(URL[]::new);
    }

}
