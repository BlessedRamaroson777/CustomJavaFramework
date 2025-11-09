package mg.itu.framework.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;

@WebServlet("/")
public class FrontController extends HttpServlet {

    private static final String ROUTES_ATTRIBUTE = "framework.routes";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        // Lire le package de base depuis web.xml
        String basePackage = config.getInitParameter("basePackage");
        if (basePackage == null || basePackage.trim().isEmpty()) {
            basePackage = "mg.itu.test.controller"; // valeur par defaut
        }
        
        // Scanner et stocker dans ServletContext
        Map<String, MethodHandler> routes = new HashMap<>();
        scanControllersInPackage(basePackage, routes);
        getServletContext().setAttribute(ROUTES_ATTRIBUTE, routes);
        
        System.out.println("Framework initialized. Found " + routes.size() + " route(s).");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // Verifier si c'est une ressource statique (js, css, html, etc.)
        if (isStaticResource(path)) {
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }

        // Recuperer les routes depuis ServletContext
        @SuppressWarnings("unchecked")
        Map<String, MethodHandler> routes = (Map<String, MethodHandler>) getServletContext()
                .getAttribute(ROUTES_ATTRIBUTE);

        // Verifier si l'URL correspond à une route
        MethodHandler handler = routes != null ? routes.get(path) : null;
        
        response.setContentType("text/plain;charset=UTF-8");
        
        if (handler != null) {
            // Afficher les informations sans invoquer
            response.getWriter().println("URL trouvee : " + path);
            response.getWriter().println("Classe : " + handler.clazz.getName());
            response.getWriter().println("Methode : " + handler.method.getName());
        } else {
            // Pas de methode pour cette URL
            response.getWriter().println("Pas de methode pour cette URL : " + path);
        }
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".js") || path.endsWith(".css") ||
                path.endsWith(".html") || path.endsWith(".htm") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".gif") || path.endsWith(".ico");
    }

    /**
     * Scans the given package for classes annotated with @MyController,
     * then scans their methods for @MyURL annotations and populates the routes map.
     */
    private void scanControllersInPackage(String basePackage, Map<String, MethodHandler> routes) {
        String packagePath = "/WEB-INF/classes/" + basePackage.replace('.', '/') + "/";
        scanControllersRecursive(packagePath, routes);
    }

    /**
     * Scans the given webapp classes directory path for .class files, builds
     * fully-qualified class names and inspects methods annotated with @MyURL.
     */
    private void scanControllersRecursive(String classesFolderPath, Map<String, MethodHandler> routes) {
        try {
            Set<String> paths = getServletContext().getResourcePaths(classesFolderPath);
            if (paths == null) return;
            for (String p : paths) {
                if (p.endsWith("/")) {
                    // recursive scan subfolders
                    scanControllersRecursive(p, routes);
                } else if (p.endsWith(".class")) {
                    // build class name from path
                    String prefix = "/WEB-INF/classes/";
                    if (!p.startsWith(prefix)) continue;
                    String className = p.substring(prefix.length(), p.length() - 6).replace('/', '.');
                    try {
                        Class<?> cls = Class.forName(className);
                        
                        // Verifier si la classe est annotee avec @MyController
                        if (!cls.isAnnotationPresent(MyController.class)) {
                            continue;
                        }
                        
                        // Scanner les methodes de cette classe
                        for (Method m : cls.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(MyURL.class)) {
                                MyURL ann = m.getAnnotation(MyURL.class);
                                String url = ann.value();
                                if (!url.startsWith("/")) url = "/" + url;
                                routes.put(url, new MethodHandler(cls, m));
                                System.out.println("  Route ajoutee : " + url + " -> " 
                                    + cls.getSimpleName() + "." + m.getName());
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        // ignore classes that cannot be loaded
                    }
                }
            }
        } catch (Exception e) {
            // don't fail init because of scanning — log to stderr
            e.printStackTrace();
        }
    }

    private static class MethodHandler {
        final Class<?> clazz;
        final Method method;

        MethodHandler(Class<?> clazz, Method method) {
            this.clazz = clazz;
            this.method = method;
        }
    }
}
