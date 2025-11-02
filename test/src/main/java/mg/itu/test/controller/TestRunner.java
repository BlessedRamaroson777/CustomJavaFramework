package mg.itu.test.controller;

import java.util.List;

import itu.framework.annotations.MyController;
import itu.framework.scan.ControllerScanner;

@MyController("TestRunner")
public class TestRunner {

    public static void main(String[] args) {
        List<Class<?>> controllers = ControllerScanner.findControllersInClassPath(ControllerScanner.class);
        System.out.println("Found " + controllers.size() + " controller(s):");
        for (Class<?> c : controllers) {
            MyController ann = c.getAnnotation(MyController.class);
            String val = ann != null ? ann.value() : "";
            System.out.println(" - " + c.getName() + " -> " + val);
        }
    }

}
