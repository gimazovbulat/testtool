package testtool.data;

import java.io.File;
import java.util.Collections;
import java.util.Set;

/**
 * Class representation for not valid java class
 * */
public class BrokenClass extends BaseClassDto {
//	java class name
	private final String classname;

	public BrokenClass(String classname) {
		this.classname = classname;
	}

//	null file
	public File getFile() {
		return null;
	}

	//	no dependencies
	public Set<String> getDependencies() {
		return Collections.emptySet();
	}

	public String getName() {
		return classname;
	}

	//not a test
	public boolean isATest() {
		return false;
	}

	//not located in file
	public boolean locatedInClassFile() {
		return false;
	}
}
