package testtool.util;

/**
 * pair of listener and clazz
 * */
public class PairOfListenerAndClass {
//    listener
    private final Object listener;
//    clzzz
    private final Class<?> clazz;

    public PairOfListenerAndClass(Class<?> clazz, Object listener) {
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PairOfListenerAndClass && ((PairOfListenerAndClass) obj).listener.equals(listener) && ((PairOfListenerAndClass) obj).clazz.equals(clazz);
    }


    @Override
    public int hashCode() {
//    for hashcode we need both listener and clazz
        return clazz.hashCode() ^ listener.hashCode();
    }
}
