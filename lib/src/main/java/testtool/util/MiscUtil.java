package testtool.util;

import testtool.data.BaseClassDto;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MiscUtil {

    public static String validateFileName(String path) {
        return path.replace("\\", "/");
    }

    /*
     * if matches pattern *.class
     * */
    public static boolean isClass(File pathname) {
        return pathname
            .getAbsolutePath()
            .matches(".*\\.[Cc][Ll][Aa][Ss][Ss]\\z");
    }

    /**
     * map classes to their names
     *
     * @param classes - list of classes
     */
    public static List<String> classesToNames(Collection<BaseClassDto> classes) {
//        go through classes
        return classes.stream()
//            map to their names
            .map(BaseClassDto::getName)
//            collect to list
            .collect(Collectors.toList());
    }
}
