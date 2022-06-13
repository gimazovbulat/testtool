package testtool.util;

public class DescriptorChanger {

    public static String parseClassNameFromConstantPoolDescr(String descr) {
        String newDescr = descr.replaceAll("\\[", "");
        newDescr = newDescr.startsWith("L") ?
            newDescr.replaceFirst("L", "") :
            newDescr;

        return newDescr.length() == 1 ?
            Object.class.getName() :
            newDescr.replace(";", "").replace('/', '.');
    }
}
