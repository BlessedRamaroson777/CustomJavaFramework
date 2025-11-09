package mg.itu.framework.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import mg.itu.framework.ModelView;

@WebServlet("/")
public class FrontController extends HttpServlet {

    // Simple route table: url -> handler (class + method)
    private final Map<String, MethodHandler> routes = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Scan a default controllers folder inside WEB-INF/classes
        // You can change this path if your controllers are elsewhere
        scanControllers("/WEB-INF/classes/mg/itu/test/controller/");
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

        // Verifier table de routage
        MethodHandler handler = routes.get(path);
        if (handler != null) {
            System.out.println("[FrontController] URL trouvee : " + path + " -> " 
                + handler.clazz.getName() + "." + handler.method.getName());
            try {
                Object instance = handler.clazz.getDeclaredConstructor().newInstance();
                Object ret = handler.method.invoke(instance);
                
                // Verifier le type de retour
                if (ret instanceof ModelView) {
                    System.out.println("[FrontController] Methode invoquee avec succes, retour ModelView");
                    ModelView mv = (ModelView) ret;
                    String viewPath = "/WEB-INF/views/" + mv.getView();
                    
                    // Verifier si la page JSP existe
                    if (getServletContext().getResource(viewPath) == null) {
                        System.out.println("[FrontController] Page JSP introuvable : " + viewPath);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page JSP introuvable : " + mv.getView());
                    } else {
                        request.getRequestDispatcher(viewPath).forward(request, response);
                    }
                } else if (ret instanceof String) {
                    System.out.println("[FrontController] Methode invoquee avec succes, retour String");
                    // Stocker le resultat dans request et forward vers JSP
                    request.setAttribute("result", ret);
                    request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);
                } else {
                    System.out.println("[FrontController] La methode ne retourne pas un String ou ModelView");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Methode non supportee");
                }
                return;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                System.err.println("[FrontController] Erreur lors de l'invocation : " + e.getMessage());
                e.printStackTrace();
                throw new ServletException("Erreur lors de l'invocation du handler", e);
            }
        }

        // Default fallback
        System.out.println("[FrontController] Aucune route pour : " + path);
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Pas de methode pour cette URL");
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".js") || path.endsWith(".css") ||
                path.endsWith(".html") || path.endsWith(".htm") ||
                path.endsWith(".png") || path.endsWith(".jpg") ||
                path.endsWith(".gif") || path.endsWith(".ico");
    }

    /**
     * Scans the given webapp classes directory path for .class files, builds
     * fully-qualified class names and inspects methods annotated with
     * itu.framework.annotations.MyURL to populate the routes map.
     *
     * This is a minimal implementation that works when classes are exploded
     * under WEB-INF/classes. If your controllers are packaged differently
     * (in jars), you should extend this logic.
     */
    private void scanControllers(String classesFolderPath) {
        try {
            Set<String> paths = getServletContext().getResourcePaths(classesFolderPath);
            if (paths == null) return;
            for (String p : paths) {
                if (p.endsWith("/")) {
                    // recursive scan subfolders
                    scanControllers(p);
                } else if (p.endsWith(".class")) {
                    // build class name from path
                    String prefix = "/WEB-INF/classes/";
                    if (!p.startsWith(prefix)) continue;
                    String className = p.substring(prefix.length(), p.length() - 6).replace('/', '.');
                    try {
                        Class<?> cls = Class.forName(className);
                        for (Method m : cls.getDeclaredMethods()) {
                            if (m.isAnnotationPresent(itu.framework.annotations.MyURL.class)) {
                                itu.framework.annotations.MyURL ann = m
                                        .getAnnotation(itu.framework.annotations.MyURL.class);
                                String url = ann.value();
                                if (!url.startsWith("/")) url = "/" + url;
                                routes.put(url, new MethodHandler(cls, m));
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
