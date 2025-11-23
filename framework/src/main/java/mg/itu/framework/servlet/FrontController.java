package mg.itu.framework.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mg.itu.framework.ModelView;

@WebServlet("/")
public class FrontController extends HttpServlet {

    // Route entries supporting patterns with placeholders like /users/{id}
    private final List<RouteEntry> routeEntries = new ArrayList<>();

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

        // Find route and invoke handler if any
        RouteMatch match = findRoute(path);
        if (match != null) {
            MethodHandler mh = match.handler;
            Map<String,String> pathParams = match.pathParams;
            System.out.println("[FrontController] URL trouvee : " + path + " -> " + mh.clazz.getName() + "." + mh.method.getName());
            try {
                // expose path params before invocation
                setPathParamsToRequest(pathParams, request);

                Object instance = mh.clazz.getDeclaredConstructor().newInstance();
                Object[] args = prepareMethodArgs(mh.method, match.paramNames, pathParams, request);
                Object ret = mh.method.invoke(instance, args);

                // handle return types
                if (ret instanceof ModelView) {
                    ModelView mv = (ModelView) ret;
                    copyModelToRequest(mv, request);
                    String viewPath = "/WEB-INF/views/" + mv.getView();
                    if (getServletContext().getResource(viewPath) == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page JSP introuvable : " + mv.getView());
                    } else {
                        getServletContext().getRequestDispatcher(viewPath).forward(request, response);
                    }
                } else if (ret instanceof String) {
                    request.setAttribute("result", ret);
                    request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);
                } else {
                    String retType = (ret == null) ? "null" : ret.getClass().getName();
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Methode non supportee: type de retour = " + retType);
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
                                // convert URL pattern with {param} into a regex and collect param names
                                List<String> paramNames = new ArrayList<>();
                                StringBuilder regex = new StringBuilder();
                                int i = 0;
                                while (i < url.length()) {
                                    char c = url.charAt(i);
                                    if (c == '{') {
                                        int end = url.indexOf('}', i);
                                        if (end > i) {
                                            String name = url.substring(i + 1, end);
                                            paramNames.add(name);
                                            regex.append("([^/]+)");
                                            i = end + 1;
                                            continue;
                                        }
                                    }
                                    // escape regex special chars for safety
                                    if (".[](){}+?^$\\|".indexOf(c) >= 0) {
                                        regex.append('\\').append(c);
                                    } else {
                                        regex.append(c);
                                    }
                                    i++;
                                }
                                Pattern compiled = Pattern.compile("^" + regex.toString() + "$");
                                routeEntries.add(new RouteEntry(compiled, paramNames, new MethodHandler(cls, m)));
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

    private static class RouteEntry {
        final Pattern pattern;
        final List<String> paramNames;
        final MethodHandler handler;

        RouteEntry(Pattern pattern, List<String> paramNames, MethodHandler handler) {
            this.pattern = pattern;
            this.paramNames = paramNames;
            this.handler = handler;
        }
    }

    // Utility to convert a String to the requested parameter type (primitive or wrapper or String)
    private Object convertStringToType(String s, Class<?> type) {
        if (type == String.class) return s;
        if (type == int.class || type == Integer.class) return Integer.valueOf(Integer.parseInt(s));
        if (type == long.class || type == Long.class) return Long.valueOf(Long.parseLong(s));
        if (type == boolean.class || type == Boolean.class) return Boolean.valueOf(Boolean.parseBoolean(s));
        if (type == double.class || type == Double.class) return Double.valueOf(Double.parseDouble(s));
        if (type == float.class || type == Float.class) return Float.valueOf(Float.parseFloat(s));
        if (type == short.class || type == Short.class) return Short.valueOf(Short.parseShort(s));
        if (type == byte.class || type == Byte.class) return Byte.valueOf(Byte.parseByte(s));
        if (type == char.class || type == Character.class) return s.length() > 0 ? Character.valueOf(s.charAt(0)) : Character.valueOf('\u0000');
        // For other types, no conversion supported — return null
        return null;
    }

    // Helper: result of matching a route
    private static class RouteMatch {
        final MethodHandler handler;
        final Map<String,String> pathParams;
        final List<String> paramNames;

        RouteMatch(MethodHandler handler, Map<String,String> pathParams, List<String> paramNames) {
            this.handler = handler;
            this.pathParams = pathParams;
            this.paramNames = paramNames;
        }
    }

    // Find the first matching route for the path and return extracted params
    private RouteMatch findRoute(String path) {
        for (RouteEntry re : routeEntries) {
            Matcher m = re.pattern.matcher(path);
            if (m.matches()) {
                Map<String,String> params = new HashMap<>();
                for (int i = 0; i < re.paramNames.size(); i++) {
                    String val = m.group(i + 1);
                    String name = re.paramNames.get(i);
                    params.put(name, val);
                }
                return new RouteMatch(re.handler, params, re.paramNames);
            }
        }
        return null;
    }

    private void setPathParamsToRequest(Map<String,String> pathParams, HttpServletRequest request) {
        if (pathParams == null || pathParams.isEmpty()) return;
        for (Map.Entry<String,String> pe : pathParams.entrySet()) {
            String pname = pe.getKey();
            String pval = pe.getValue();
            if (pval != null && !"null".equals(pval) && !pval.isEmpty()) {
                request.setAttribute(pname, pval);
                System.out.println("[FrontController] Path param set: " + pname + " = " + pval);
            } else {
                System.out.println("[FrontController] Path param '" + pname + "' ignored (value='" + pval + "')");
            }
        }
    }

    private Object[] prepareMethodArgs(Method method, List<String> paramNames, Map<String,String> pathParams, HttpServletRequest request) {
        java.lang.reflect.Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];
        // track which request parameter names we've consumed in the heuristic fallback
        java.util.Set<String> consumedRequestParams = new java.util.HashSet<>();
        for (int pi = 0; pi < params.length; pi++) {
            java.lang.reflect.Parameter p = params[pi];
            String pname = p.getName();
            String sval = null;
            // If parameter is annotated with @ReqParam, use its value as the key
            itu.framework.annotations.ReqParam reqAnn = p.getAnnotation(itu.framework.annotations.ReqParam.class);
            if (reqAnn != null) {
                String key = reqAnn.value();
                // prefer path param then request param
                if (pathParams != null && pathParams.containsKey(key)) sval = pathParams.get(key);
                if (sval == null) sval = request.getParameter(key);
            } else {
                if (pathParams != null && pathParams.containsKey(pname)) sval = pathParams.get(pname);
                if (sval == null) sval = request.getParameter(pname);
            }
            // fallback: if parameter name not available (compiled without -parameters),
            // use the route param order: if paramNames list contains an entry at this index, use it
            if ((sval == null || "null".equals(sval) || sval.isEmpty()) && paramNames != null && pi < paramNames.size()) {
                String pn = paramNames.get(pi);
                if (pathParams != null && pathParams.containsKey(pn)) {
                    sval = pathParams.get(pn);
                }
            }
            // Further fallback: try to match request parameters heuristically by type
            if ((sval == null || "null".equals(sval) || sval.isEmpty())) {
                java.util.Map<String,String[]> rpm = request.getParameterMap();
                for (String rpName : rpm.keySet()) {
                    if (consumedRequestParams.contains(rpName)) continue;
                    String candidate = request.getParameter(rpName);
                    if (candidate == null) continue;
                    Object converted = null;
                    try {
                        converted = convertStringToType(candidate, p.getType());
                    } catch (Exception ex) {
                        converted = null;
                    }
                    if (converted != null) {
                        sval = candidate;
                        consumedRequestParams.add(rpName);
                        break;
                    }
                }
            }
            Class<?> ptype = p.getType();
            Object aval = null;
            if (sval != null && !"null".equals(sval) && !sval.isEmpty()) {
                try {
                    aval = convertStringToType(sval, ptype);
                } catch (Exception ex) {
                    System.out.println("[FrontController] Warning: cannot convert parameter '" + pname + "' value='" + sval + "' to " + ptype.getName());
                    aval = null;
                }
            } else {
                if (ptype.isPrimitive()) {
                    if (ptype == boolean.class) aval = Boolean.FALSE;
                    else if (ptype == char.class) aval = Character.valueOf('\u0000');
                    else aval = Integer.valueOf(0);
                } else {
                    aval = null;
                }
            }
            args[pi] = aval;
            System.out.println("[FrontController] Resolved param '" + pname + "' -> " + args[pi]);
        }
        return args;
    }

    private void copyModelToRequest(ModelView mv, HttpServletRequest request) {
        try {
            java.lang.reflect.Method getter = null;
            String[] candidates = {"getMapData", "getmapData", "getMaqData"};
            for (String name : candidates) {
                try {
                    getter = mv.getClass().getMethod(name);
                    break;
                } catch (NoSuchMethodException nsme) {
                    // try next
                }
            }
            if (getter != null) {
                Object mapObj = getter.invoke(mv);
                if (mapObj instanceof Map) {
                    for (Object objEntry : ((Map<?, ?>) mapObj).entrySet()) {
                        Map.Entry<?, ?> e = (Map.Entry<?, ?>) objEntry;
                        if (e.getKey() != null) {
                            request.setAttribute(e.getKey().toString(), e.getValue());
                            System.out.println("[FrontController] Model attribute set: " + e.getKey() + " = " + e.getValue());
                        }
                    }
                    request.setAttribute("mapData", mapObj);
                    System.out.println("[FrontController] mapData attribute exposed to JSP (size=" + ((Map<?,?>)mapObj).size() + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
