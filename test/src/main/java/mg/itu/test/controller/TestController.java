package mg.itu.test.controller;

import java.util.List;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.scan.ControllerScanner;
import mg.itu.framework.ModelView;

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
    
    @MyURL("/void-test")
    public void voidMethod() {
        System.out.println("[TestController] voidMethod() appelee - ne retourne rien");
    }
    
    @MyURL("/page-test")
    public ModelView pageTest() {
        System.out.println("[TestController] pageTest() appelee - retourne ModelView");
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
        return mv;
    }
    
    @MyURL("/page-inexistante")
    public ModelView pageInexistante() {
        System.out.println("[TestController] pageInexistante() appelee - retourne ModelView avec page qui existe pas");
        ModelView mv = new ModelView();
        mv.setView("pagetrouvable.jsp");
        return mv;
    }

}
