package testtool.service.impl;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import javassist.CtClass;
import testtool.data.BaseClassDto;
import testtool.data.BrokenClass;
import testtool.data.CacheClass;
import testtool.data.JavaClassDtoImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/***
 * service to work with classes
 * */
public class ClassService {

    /**
     * map to hold info about class name vs class
     */
    private final Map<String, BaseClassDto> nameVsClass = new HashMap<>();
    //    cahced classes - path vs class
    private final Map<String, CacheClass> pathVsCacheClass = new HashMap<>();

    //    pool service
    private final ClassPoolSingleton classPoolSingleton;

    public ClassService(String classPath) {
        this.classPoolSingleton = new ClassPoolSingleton(classPath);
    }

    /**
     * get class by name
     */
    public BaseClassDto getClassByName(String className) {
        try {
            //get class
            BaseClassDto clazz = nameVsClass.get(className);
            BaseClassDto res = null;
            if (Objects.isNull(clazz)) {
                //get from cache
                CtClass classByNaem = getCached(className);
                if (Objects.isNull(classByNaem.getClassFile2())) {
                    //if no class then
                    res = new BrokenClass(className);
                } else {
                    try {
                        //create class dto
                        JavaClassDtoImpl javaClassDto = new JavaClassDtoImpl(classByNaem);
                        //get url of class
                        URL urlOfClass = classPoolSingleton.getClassPool().find(className);
//                        if url valid and protocol is file
                        if ((Objects.nonNull(urlOfClass)) && urlOfClass.getProtocol().equals("file")) {
//                           set file to class dto
                            javaClassDto.setFile(new File(urlOfClass.toURI()));
                        }
                        //set res
                        res = javaClassDto;
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException(e);
                    }
                }
                //put classname vs class
                nameVsClass.put(className, res);
            }
            //return found class
            return res;
        } catch (Exception ex) {
            return new BrokenClass(className);
        }
    }

    /**
     * return classname of changed
     */
    public String classFileChanged(File file) {
        try {
//            get hash for file content
            String hash = Files.asByteSource(file).hash(Hashing.sha256()).toString();
//          get cached class
            CacheClass cached = pathVsCacheClass.get(file.getAbsolutePath());
//            if cached not null and hashes identical
            if ((Objects.nonNull(cached)) && (cached.getHashcode().equals(hash))) {
//               return name of cached
                return cached.getName();
            }
//            create class and get name
            String classname = createClass(file).getName();
            //remove class by name
            nameVsClass.remove(classname);
//            put classname vs class
            pathVsCacheClass.put(file.getAbsolutePath(), new CacheClass(hash, classname));

//            reurn name of class
            return classname;
        } catch (Exception ex) {
            return null;
        }
    }

    //    get cached class by name
    private CtClass getCached(String className) {
        //if null then rhrow ex
        Optional<CtClass> optionalCtClass = Optional.ofNullable(classPoolSingleton.getClassPool().getOrNull(className));
        optionalCtClass.orElseThrow(() -> new IllegalStateException("Class not found " + className));
        return optionalCtClass.get();
    }

    //    create class from file
    private CtClass createClass(File file) throws IOException {
        CtClass res;
        try (FileInputStream inputStream = new FileInputStream(file)) {
//            make class from file
            res = classPoolSingleton.getClassPool().makeClass(inputStream);
        }
        return res;
    }

    public Map<String, BaseClassDto> getNameVsClass() {
        return nameVsClass;
    }

    public Map<String, CacheClass> getPathVsCacheClass() {
        return pathVsCacheClass;
    }

    public ClassPoolSingleton getClassPoolSingleton() {
        return classPoolSingleton;
    }
}
