package mg.itu.test.controller;

import java.util.List;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.scan.ControllerScanner;

@MyController("TestController")
public class TestController {
    
    @MyURL("/index")
    public String index() {
        return "Hello from Test Controller!";
    }
    
    @MyURL("/test")
    public String test() {
        // return "Test method called!";
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
    
}
