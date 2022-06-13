package testtool.data;

import java.io.File;
import java.util.Set;

/**
 * base representation for java class
 */
public abstract class BaseClassDto {

    //    equlas if same name
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BaseClassDto) &&
            ((BaseClassDto) obj).getName().equals(getName());
    }

    //    hashcode only with name
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    //    get name of class
    public abstract String getName();

    public abstract Set<String> getDependencies();

    //    whether test or not
    public abstract boolean isATest();

    public abstract boolean locatedInClassFile();

    //    get file of class
    public abstract File getFile();
}
