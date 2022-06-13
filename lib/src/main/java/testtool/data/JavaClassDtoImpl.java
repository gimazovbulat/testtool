package testtool.data;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import org.junit.jupiter.api.Test;
import testtool.util.DescriptorChanger;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javassist.bytecode.AnnotationsAttribute.visibleTag;

/**
 * class to work with class, find dependencies
 */
public class JavaClassDtoImpl extends BaseClassDto {

    //	dependencies fo class such class annotations, method annotations
    private final Set<String> dependencies;
    //    whether class is test o not
    private final boolean isATest;
    //    class name
    private final String name;
    //    file of class
    private File file;

    public JavaClassDtoImpl(CtClass clazz) {
//        set dependencies
        dependencies = findDependencies(clazz);
//        if class has tests and annotated with junit.Test annoration}
        isATest = classHasTests(clazz) && dependencies.contains(Test.class.getName());
        name = clazz.getName();
    }

    //    find dependencies for class
    private Set<String> findDependencies(CtClass clazz) {
        Set<String> dependencies = new HashSet<>();
//        add constants to dependencies
        addConstants(clazz, dependencies);
//        add fields to dependencies
        addFields(clazz, dependencies);
//        add class annotations to dependencies
        addClassAnnotations(clazz, dependencies);
//        add field annotations to dependencies
        addFieldAnnotations(clazz, dependencies);
//        add method annotations to dependencies
        addMethodAnnotations(clazz, dependencies);

        return dependencies;
    }

    /**
     * add field annotations to dependencies
     *
     * @param clazz        - clazz for which field annotations should be found
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addFieldAnnotations(CtClass clazz, Collection<String> dependencies) {
//        go through declared fields
        Arrays.stream(clazz.getDeclaredFields()).forEach(
            field -> {
//                get attributes of field
                List<?> attributes = field.getFieldInfo2().getAttributes();
//                add only annotations
                addAnnotationsForAttrs(dependencies, attributes);
            }
        );
    }

    /**
     * add fields to dependencies
     *
     * @param clazz        - clazz for which field annotations should be found
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addFields(CtClass clazz, Collection<String> dependencies) {
//        go through fields
        Arrays.stream(clazz.getDeclaredFields()).forEach(
            field -> {
//                add fields to dependencies
                dependencies.add(
                    DescriptorChanger.parseClassNameFromConstantPoolDescr(
                        field.getFieldInfo2().getDescriptor()
                    ));
            }
        );
    }

    /**
     * add method annotations to dependencies
     *
     * @param clazz        - clazz for which field annotations should be found
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addMethodAnnotations(CtClass clazz, Collection<String> dependencies) {
//        go through methods
        Arrays.stream(clazz.getDeclaredMethods()).forEach(
            m -> {
//                get method info
                MethodInfo method = m.getMethodInfo2();
//                get attributes
                List<?> attrs = method.getAttributes();
//                add annotations for attributes
                addAnnotationsForAttrs(dependencies, attrs);
//                add param annotations for method
                addParamAnnotations(dependencies, method);
            }
        );
    }

    /**
     * add annotations for attribues
     *
     * @param attrs        - attributes
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addAnnotationsForAttrs(Collection<String> dependencies, List<?> attrs) {
        attrs.stream()
//            get only annotations
            .filter(a -> a instanceof AnnotationsAttribute)
//            add annotations to dependencies
            .forEach(a -> addAnnotations(dependencies, (AnnotationsAttribute) a));
    }

    private void addParamAnnotations(Collection<String> dependencies, MethodInfo methodInfo) {
        AttributeInfo attribute = methodInfo.getAttribute(ParameterAnnotationsAttribute.visibleTag);
        ParameterAnnotationsAttribute annotationAttribute = (ParameterAnnotationsAttribute) attribute;
        if (annotationAttribute != null) {
            Annotation[][] parameters = annotationAttribute.getAnnotations();
            for (Annotation[] annotations : parameters) {
                for (Annotation annotation : annotations) {
                    dependencies.add(annotation.getTypeName());
                }
            }
        }
    }

    /**
     * add class annotations
     *
     * @param clazz        - clazz for which field annotations should be found
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addClassAnnotations(CtClass clazz, Collection<String> dependencies) {
//        get only runtime visible annotations
//add them to dependencies
        addAnnotations(dependencies, (AnnotationsAttribute) clazz.getClassFile2().getAttribute(visibleTag));
    }

    /**
     * add annotations
     *
     * @param annotationsAttr annotation attributes
     * @param dependencies    - list of dependencies to which current should be added
     */
    private void addAnnotations(Collection<String> dependencies, AnnotationsAttribute annotationsAttr) {
//        if not null
        if (Objects.nonNull(annotationsAttr)) {
//            add annotations
            Arrays.stream(annotationsAttr.getAnnotations()).forEach(dep -> dependencies.add(dep.getTypeName()));
        }
    }

    /**
     * add constants to dependencies
     *
     * @param clazz        - clazz for which field annotations should be found
     * @param dependencies - list of dependencies to which current should be added
     */
    private void addConstants(CtClass clazz, Collection<String> dependencies) {
//        get constant pool
        clazz.getClassFile2().getConstPool()
//            get class names to go through
            .getClassNames()
            .forEach(dep -> dependencies.add(dep.replace('/', '.')));
    }

    /**
     * whether has tests or not
     *
     * @param clazzRef - class to check
     */
    private boolean classHasTests(CtClass clazzRef) {
//        go through class methods
        for (CtMethod m : clazzRef.getMethods()) {
//            if junit then return true
            if (isJunitMethod(m)) {
                return true;
            }
        }
//        there are no tests
        return false;
    }

    /**
     * is method is junit
     *
     * @param method - method of class
     */
    private boolean isJunitMethod(CtMethod method) {
//        get attributes of method
        final List<?> attrs = method.getMethodInfo2().getAttributes();
//        go through attributes
        return attrs.stream()
//            if attribute is annotation
            .filter(clazz -> clazz instanceof AnnotationsAttribute)
            .map(a -> {
//                stream as attribute annotations
                AnnotationsAttribute attr = (AnnotationsAttribute) a;
                return attr.getAnnotations();
            })
            .flatMap(Arrays::stream)
            .map(Annotation::getTypeName)
//            if has junit Test annotation
            .anyMatch(Test.class.getName()::equals);
    }

    /**
     * whether located in file
     */
    @Override
    public boolean locatedInClassFile() {
        return file != null;
    }

    /**
     * get file of class
     */
    @Override
    public File getFile() {
        return file;
    }

    /**
     * get name of class
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * whether class is a test
     */
    @Override
    public boolean isATest() {
        return isATest;
    }

    /**
     * return class name
     */
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
