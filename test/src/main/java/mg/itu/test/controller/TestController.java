package mg.itu.test.controller;

import java.util.List;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.scan.ControllerScanner;

@MyController("TestController")
public class TestController {
    
    @MyURL("/index")
    public String index() {
        System.out.println("[TestController] index() appelee");
        return "Hello from Test Controller!";
    }
    
    @MyURL("/test")
    public String test() {
        System.out.println("[TestController] test() appelee");
        List<Class<?>> controllers = ControllerScanner.findControllersInClassPath(ControllerScanner.class);
        StringBuilder sb = new StringBuilder();

        sb.append("Test method called!\n");
        sb.append("Found ").append(controllers.size()).append(" controller(s):\n");

        for (Class<?> c : controllers) {
            MyController ann = c.getAnnotation(MyController.class);
            String val = (ann != null) ? ann.value() : "";
            sb.append(" - ").append(c.getName()).append(" -> ").append(val).append("\n");
        }

        return sb.toString();
    }
    
    @MyURL("/hello")
    public String hello() {
        System.out.println("[TestController] hello() appelee");
        return "Hello World!";
    }
    
    @MyURL("/void-test")
    public void voidMethod() {
        System.out.println("[TestController] voidMethod() appelee - ne retourne rien");
    }
    
    @MyURL("/integer-test")
    public Integer integerMethod() {
        System.out.println("[TestController] integerMethod() appelee - retourne un Integer");
        return 42;
    }

}
