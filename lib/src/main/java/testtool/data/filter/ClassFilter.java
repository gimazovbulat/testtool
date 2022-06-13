package testtool.data.filter;

import java.io.File;
import java.io.FileFilter;

import static testtool.util.MiscUtil.isClass;

/**
 * filter to find classes
 */
public class ClassFilter implements FileFilter {

    /**
     * accept if class or directory
     */
    @Override
    public boolean accept(File pathname) {
        return isClass(pathname) || pathname.isDirectory();
    }
}
