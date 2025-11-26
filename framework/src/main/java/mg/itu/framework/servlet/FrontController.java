package itu.framework.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
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

import itu.framework.ModelView;

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

        // Find exact route candidate first (matching HTTP method too); if none, fall back to parameterized route matching
        String httpMethod = request.getMethod();
        RouteMatch match = findExactRoute(path, httpMethod);
        if (match == null) {
            match = findBestRoute(path, request);
        }
        if (match != null) {
            MethodHandler mh = match.handler;
            Map<String,String> pathParams = match.pathParams;
            System.out.println("[FrontController] URL trouvee : " + path + " -> " + mh.clazz.getName() + "." + mh.method.getName());
            try {
                // expose path params before invocation
                setPathParamsToRequest(pathParams, request);

                Object instance = mh.clazz.getDeclaredConstructor().newInstance();
                Object[] args = prepareMethodArgs(mh.method, match.paramNames, pathParams, request);
                Object ret = null;
                try {
                    // ensure argument types are compatible with parameter types
                    Object[] coerced = coerceArgsToParameterTypes(mh.method, args);
                    ret = mh.method.invoke(instance, coerced);
                } catch (IllegalArgumentException iae) {
                    System.err.println("[FrontController] IllegalArgumentException invoking method: " + mh.method.getName());
                    System.err.println("[FrontController] Expected param types:");
                    for (Class<?> t : mh.method.getParameterTypes()) System.err.println("  - " + t.getName());
                    System.err.println("[FrontController] Provided argument values/types:");
                    for (int i = 0; i < args.length; i++) {
                        Object a = args[i];
                        String tname = (a == null) ? "null" : a.getClass().getName();
                        System.err.println("  arg[" + i + "] = " + a + " (" + tname + ")");
                    }
                    throw iae;
                }

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

    // Find an exact (non-parameterized) route for the path. If none found, return null.
    private RouteMatch findExactRoute(String path, String httpMethod) {
        for (RouteEntry re : routeEntries) {
            if (re.paramNames != null && !re.paramNames.isEmpty()) continue; // skip parameterized
            // check allowed methods if present
            if (re.allowedMethods != null && !re.allowedMethods.isEmpty() && !re.allowedMethods.contains(httpMethod)) continue;
            Matcher m = re.pattern.matcher(path);
            if (m.matches()) {
                return new RouteMatch(re.handler, new HashMap<>(), re.paramNames);
            }
        }
        return null;
    }
    
    private int evaluateCandidate(Method method, List<String> paramNames, Map<String,String> pathParams, HttpServletRequest request) {
        java.lang.reflect.Parameter[] params = method.getParameters();
        int score = 0;
        java.util.Map<String,String[]> rpm = request.getParameterMap();
        for (int pi = 0; pi < params.length; pi++) {
            java.lang.reflect.Parameter p = params[pi];
            String pname = p.getName();
            boolean resolved = false;
            // 1) path param with same name
            if (pathParams != null && pname != null && pathParams.containsKey(pname)) {
                score += 50; resolved = true;
            }
            // 2) request parameter matching param name
            if (!resolved && pname != null && request.getParameter(pname) != null) {
                score += 30; resolved = true;
            }
            // 3) annotated ReqParam
            itu.framework.annotations.ReqParam reqAnn = p.getAnnotation(itu.framework.annotations.ReqParam.class);
            if (!resolved && reqAnn != null) {
                String key = reqAnn.value();
                if (pathParams != null && pathParams.containsKey(key)) { score += 40; resolved = true; }
                else if (request.getParameter(key) != null) { score += 20; resolved = true; }
            }
            // 4) index route param (if compiled param names not present)
            if (!resolved && paramNames != null && pi < paramNames.size()) {
                String pn = paramNames.get(pi);
                if (pathParams != null && pathParams.containsKey(pn)) { score += 40; resolved = true; }
            }
            // 5) heuristic: any request param convertible to target type
            if (!resolved && rpm != null && !rpm.isEmpty()) {
                for (String rpName : rpm.keySet()) {
                    String cand = request.getParameter(rpName);
                    if (cand == null) continue;
                    Object conv = null;
                    try { conv = convertStringToType(cand, p.getType()); } catch (Exception ex) { conv = null; }
                    if (conv != null) { score += 10; resolved = true; break; }
                }
            }
            // small bonus for nullable non-primitive (can be left null)
            if (!resolved && !p.getType().isPrimitive()) score += 5;
            // penalize if primitive and not resolved (less likely to be appropriate)
            if (!resolved && p.getType().isPrimitive()) score -= 50;
        }
        // slight penalty for longer parameter lists (prefer simpler matches)
        score -= params.length;
        return score;
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
                                String[] methods = ann.methods();
                                List<String> allowed = (methods != null && methods.length > 0) ? Arrays.asList(methods) : null;
                                routeEntries.add(new RouteEntry(compiled, paramNames, new MethodHandler(cls, m), allowed));
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
        final List<String> allowedMethods; // null or empty = any

        RouteEntry(Pattern pattern, List<String> paramNames, MethodHandler handler, List<String> allowedMethods) {
            this.pattern = pattern;
            this.paramNames = paramNames;
            this.handler = handler;
            this.allowedMethods = allowedMethods;
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

    // Coerce provided argument objects to the target parameter types where possible.
    // This helps avoid IllegalArgumentException when wrappers/numeric types differ.
    private Object[] coerceArgsToParameterTypes(Method method, Object[] args) {
        Class<?>[] ptypes = method.getParameterTypes();
        Object[] res = new Object[args.length];
        for (int i = 0; i < ptypes.length; i++) {
            Class<?> tgt = ptypes[i];
            Object a = args[i];
            if (a == null) {
                if (tgt.isPrimitive()) {
                    if (tgt == boolean.class) res[i] = Boolean.FALSE;
                    else if (tgt == char.class) res[i] = Character.valueOf('\u0000');
                    else if (tgt == byte.class) res[i] = Byte.valueOf((byte) 0);
                    else if (tgt == short.class) res[i] = Short.valueOf((short) 0);
                    else if (tgt == int.class) res[i] = Integer.valueOf(0);
                    else if (tgt == long.class) res[i] = Long.valueOf(0L);
                    else if (tgt == float.class) res[i] = Float.valueOf(0f);
                    else if (tgt == double.class) res[i] = Double.valueOf(0d);
                    else res[i] = null;
                } else {
                    res[i] = null;
                }
                continue;
            }

            // already compatible
            if (tgt.isInstance(a) || (tgt.isPrimitive() && isWrapperOf(a.getClass(), tgt))) {
                res[i] = a;
                continue;
            }

            // number -> numeric conversions
            if (a instanceof Number) {
                Number n = (Number) a;
                if (tgt == int.class || tgt == Integer.class) res[i] = Integer.valueOf(n.intValue());
                else if (tgt == long.class || tgt == Long.class) res[i] = Long.valueOf(n.longValue());
                else if (tgt == double.class || tgt == Double.class) res[i] = Double.valueOf(n.doubleValue());
                else if (tgt == float.class || tgt == Float.class) res[i] = Float.valueOf(n.floatValue());
                else if (tgt == short.class || tgt == Short.class) res[i] = Short.valueOf(n.shortValue());
                else if (tgt == byte.class || tgt == Byte.class) res[i] = Byte.valueOf(n.byteValue());
                else if (tgt == char.class || tgt == Character.class) res[i] = Character.valueOf((char) n.intValue());
                else res[i] = a;
                continue;
            }

            // string -> convert
            if (a instanceof String) {
                Object conv = convertStringToType((String) a, tgt);
                if (conv != null) { res[i] = conv; continue; }
            }

            // fallback: if assignable, cast, else keep original (may still throw on invoke)
            if (tgt.isAssignableFrom(a.getClass())) res[i] = a; else res[i] = a;
        }
        return res;
    }

    private boolean isWrapperOf(Class<?> wrapper, Class<?> primitive) {
        return (primitive == int.class && wrapper == Integer.class)
                || (primitive == long.class && wrapper == Long.class)
                || (primitive == double.class && wrapper == Double.class)
                || (primitive == float.class && wrapper == Float.class)
                || (primitive == short.class && wrapper == Short.class)
                || (primitive == byte.class && wrapper == Byte.class)
                || (primitive == boolean.class && wrapper == Boolean.class)
                || (primitive == char.class && wrapper == Character.class);
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

    // Find the best matching route among all route entries for the given path and request.
    // Scoring heuristic: prefer methods where parameters are resolved from path or by request parameter name,
    // then methods resolving by annotation, then heuristics. Returns the RouteMatch for the chosen method.
    private RouteMatch findBestRoute(String path, HttpServletRequest request) {
        RouteMatch best = null;
        int bestScore = Integer.MIN_VALUE;
        for (RouteEntry re : routeEntries) {
            // check allowed HTTP methods for this route (null/empty = any)
            if (re.allowedMethods != null && !re.allowedMethods.isEmpty() && !re.allowedMethods.contains(request.getMethod())) continue;
            Matcher m = re.pattern.matcher(path);
            if (!m.matches()) continue;
            Map<String,String> params = new HashMap<>();
            for (int i = 0; i < re.paramNames.size(); i++) {
                String val = m.group(i + 1);
                String name = re.paramNames.get(i);
                params.put(name, val);
            }
            Method candidate = re.handler.method;
            int score = evaluateCandidate(candidate, re.paramNames, params, request);
            if (score > bestScore) {
                bestScore = score;
                best = new RouteMatch(re.handler, params, re.paramNames);
            }
        }
        return best;
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
        // (no heuristic) no need to track consumed request params
        // track binding source for each parameter (path / requestName / annotation / indexRoute / heuristic / default)
        java.util.Map<String, String> bindingSources = new java.util.HashMap<>();
        for (int pi = 0; pi < params.length; pi++) {
            java.lang.reflect.Parameter p = params[pi];
            String pname = p.getName();
            // determine a stable, human-friendly name to expose to views
            String displayName = pname;
            itu.framework.annotations.ReqParam reqAnn = p.getAnnotation(itu.framework.annotations.ReqParam.class);
            if (reqAnn != null && reqAnn.value() != null && !reqAnn.value().isEmpty()) {
                displayName = reqAnn.value();
            } else if ((pname == null || pname.startsWith("arg")) && paramNames != null && pi < paramNames.size()) {
                displayName = paramNames.get(pi);
            }
            String sval = null;
            // New precedence order (per request):
            // 1) path variable with parameter name (or annotation name if path was defined with that name)
            // 2) request parameter with the method parameter name
            // 3) @ReqParam annotation (request parameter with annotation value)
            // 1) try path params (prefer exact param name)
            if (pathParams != null && pathParams.containsKey(displayName)) {
                sval = pathParams.get(displayName);
                bindingSources.put(displayName, "path");
            }
            // 2) try request parameter by method parameter name
            if ((sval == null || "null".equals(sval) || sval.isEmpty())) {
                String r = request.getParameter(displayName);
                if (r != null && !r.isEmpty()) {
                    sval = r;
                    bindingSources.put(displayName, "requestName");
                }
            }
            // 3) if annotated with @ReqParam and still not found, try annotation value
            if ((sval == null || "null".equals(sval) || sval.isEmpty()) && reqAnn != null) {
                String key = reqAnn.value();
                if (pathParams != null && pathParams.containsKey(key)) {
                    sval = pathParams.get(key);
                    bindingSources.put(displayName, "path");
                }
                if ((sval == null || "null".equals(sval) || sval.isEmpty())) {
                    String r2 = request.getParameter(key);
                    if (r2 != null && !r2.isEmpty()) {
                        sval = r2;
                        bindingSources.put(displayName, "annotation");
                    }
                }
            }
            // fallback: if parameter name not available (compiled without -parameters),
            // use the route param order: if paramNames list contains an entry at this index, use it
            if ((sval == null || "null".equals(sval) || sval.isEmpty()) && paramNames != null && pi < paramNames.size()) {
                String pn = paramNames.get(pi);
                if (pathParams != null && pathParams.containsKey(pn)) {
                    sval = pathParams.get(pn);
                    bindingSources.put(displayName != null ? displayName : pn, "indexRoute");
                }
            }
            // No heuristic fallback: only exact name matches, annotation names or route params are accepted.
            Class<?> ptype = p.getType();
            Object aval = null;
            if (sval != null && !"null".equals(sval) && !sval.isEmpty()) {
                try {
                    aval = convertStringToType(sval, ptype);
                        // If we already resolved a binding source earlier, keep it. If none, mark as requestName by default
                        if (!bindingSources.containsKey(displayName)) bindingSources.put(displayName, "requestName");
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
                    // mark as default/unset
                        if (!bindingSources.containsKey(displayName)) bindingSources.put(displayName, "default");
                }
            }
                args[pi] = aval;
                System.out.println("[FrontController] Resolved param '" + displayName + "' -> " + args[pi] + " (source=" + bindingSources.get(displayName) + ")");
        }
        // Expose binding sources map to request so views can display how each parameter was resolved
        request.setAttribute("bindingSources", bindingSources);
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
