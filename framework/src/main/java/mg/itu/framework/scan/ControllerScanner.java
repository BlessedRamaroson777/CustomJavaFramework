package itu.framework.scan;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import itu.framework.annotations.MyController;

/**
 * ControllerScanner: scans the classes location for types annotated with
 * @MyController. This is adapted from the example in MonProjetTest.
 */
public class ControllerScanner {

    public static List<Class<?>> findControllersInClassPath(Class<?> anchor) {
        List<Class<?>> found = new ArrayList<>();
        try {
            java.net.URL location = anchor.getProtectionDomain().getCodeSource().getLocation();
            File classesDir = new File(location.toURI());
            findClasses(classesDir, "", found, MyController.class);
        } catch (Exception e) {
            // ignore
        }
        return found;
    }

    private static void findClasses(File directory, String packageName, List<Class<?>> found,
            Class<? extends Annotation> annotation) throws ClassNotFoundException {
        if (!directory.exists()) return;
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                findClasses(file, newPackage, found, annotation);
            } else if (file.getName().endsWith(".class")) {
                String className = (packageName.isEmpty() ? "" : packageName + ".")
                        + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotation)) {
                        found.add(clazz);
                    }
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
    }
}
