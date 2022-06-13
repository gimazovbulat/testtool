package testtool.data;

/*
 * Cached class
 * */
public class CacheClass {

    /**
     * hashcode as key for cached class
     */
    final String hashcode;

    /**
     * name of the class
     */
    final String name;

    public CacheClass(String hashcode, String name) {
        this.hashcode = hashcode;
        this.name = name;
    }

    public String getHashcode() {
        return hashcode;
    }

    public String getName() {
        return name;
    }
}