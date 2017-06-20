package org.elasticsearch.util;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utility to scan a package for classes with a given annotation.
 * 
 * @author Luc Boutier
 */
public final class AnnotationScanner {
    private static final Logger LOGGER = Logger.getLogger(AnnotationScanner.class.toString());

    /** Utility classes should have private constructor. */
    private AnnotationScanner() {
    }

    /**
     * Scan a package to find classes that have the given annotation.
     * 
     * @param packageRoot The package to scan.
     * @param anno Annotation that should be on the class that we are interested in.
     * @return A set of classes that have the annotation.
     */
    public static Set<Class<?>> scan(String packageRoot, Class<? extends Annotation> anno) {

        /*FastClasspathScanner scanner = new FastClasspathScanner();
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        scanner.matchClassesWithAnnotation(anno, aClass -> classSet.add(aClass));*/

        Reflections reflections = new Reflections(packageRoot);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(anno);

        //classSet.stream().filter( aClass -> aClass.getPackage().getName().startsWith(packageRoot)).collect(Collectors.toSet());

        return annotated;
    }

    /**
     * Get an annotation on the class or one of the super classes.
     * 
     * @param annotationClass The annotation to get.
     * @param clazz The class on which to search for the annotation.
     * @return The annotation for this class or null if not found neither on the class or one of it's super class.
     */
    public static <T extends Annotation> T getAnnotation(Class<T> annotationClass, Class<?> clazz) {
        if (clazz == Object.class) {
            return null;
        }
        T annotationInstance = clazz.getAnnotation(annotationClass);
        if (annotationInstance == null) {
            return getAnnotation(annotationClass, clazz.getSuperclass());
        }
        return annotationInstance;
    }
}