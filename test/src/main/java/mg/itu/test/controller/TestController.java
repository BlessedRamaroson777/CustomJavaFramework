package mg.itu.test.controller;

import java.util.List;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.scan.ControllerScanner;
import itu.framework.ModelView;

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

    @MyURL("/page-with-data")
    public ModelView pageWithData() {
        System.out.println("[TestController] pageWithData() appelee - retourne ModelView avec des donnees");
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
        // Use reflection to add data for compatibility with multiple ModelView variants
        try {
            java.lang.reflect.Method add = null;
            // prefer addData(String,Object)
            try {
                add = mv.getClass().getMethod("addData", String.class, Object.class);
            } catch (NoSuchMethodException ex) {
                // try common overloads
                try { add = mv.getClass().getMethod("addData", String.class, String.class); } catch (NoSuchMethodException e) {}
                if (add == null) {
                    try { add = mv.getClass().getMethod("addData", String.class, int.class); } catch (NoSuchMethodException e) {}
                }
            }
            if (add != null) {
                add.invoke(mv, "message", "Hello from TestController");
                // if the available method takes int, wrap appropriately
                if (add.getParameterTypes()[1] == int.class) {
                    add.invoke(mv, "count", 42);
                } else {
                    add.invoke(mv, "count", Integer.valueOf(42));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }

    @MyURL("/users/{id}")
    public ModelView userById() {
        System.out.println("[TestController] userById() appelee - exemple de route avec {id}");
        ModelView mv = new ModelView();
        mv.setView("test.jsp");
        mv.addData("info", "Page utilisateur (exemple)");
        // On peut ajouter d'autres données pour démonstration
        mv.addData("note", "La valeur {id} sera exposée par FrontController en 'id' si présente");
        return mv;
    }

    @MyURL("/etudiant/{id}")
    public ModelView etudiant(int id) {
        System.out.println("[TestController] etudiant() appelee avec id=" + id);
        // 6 - Page fiche détail étudiant : /etudiant/{id}
        ModelView mv = new ModelView();
        mv.setView("etudiant.jsp");
        mv.addData("studentId", id);
        mv.addData("message", "Fiche detail etudiant pour id=" + id);
        return mv;
    }

    @MyURL("/inona/params")
    // etudiant/params?id=2&name=Alice
    public ModelView etudiantParams(@itu.framework.annotations.ReqParam("id") Integer id,
                                     @itu.framework.annotations.ReqParam("name") String name) {
        // 6 bis - request parameters mapping by name
        System.out.println("[TestController] etudiantParams() appelee avec id=" + id + " name=" + name);
        ModelView mv = new ModelView();
        mv.setView("etudiant_params.jsp");
        mv.addData("idParam", id);
        mv.addData("nameParam", name);
        return mv;
    }

    @MyURL("/params2")
    // etudiant/params?id=2&name=Alice
    public ModelView etudiantParams2(Integer id, String name) {
        // 6 bis - request parameters mapping by name
        System.out.println("[TestController] etudiantParams() appelee avec id=" + id + " name=" + name);
        ModelView mv = new ModelView();
        mv.setView("etudiant_param2.jsp");
        mv.addData("idParam", id);
        mv.addData("nameParam", name);
        return mv;
    }

    @MyURL(value = "/hello", methods = {"GET"})
    public String hello() {
        System.out.println("[TestController] hello() appelee");
        return "Hello from TestController (hello)";
    }

}
