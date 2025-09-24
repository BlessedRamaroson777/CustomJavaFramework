package mg.itu.framework.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/*")
public class FrontController extends HttpServlet {
    
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
        
        // Vérifier si c'est une ressource statique (js, css, html, etc.)
        if (isStaticResource(path)) {
            request.getRequestDispatcher(path).forward(request, response);
            return;
        }
        
        // Traitement des URLs dynamiques par le framework
        // TODO: Implémenter la logique de routage vers les méthodes
        response.setContentType("text/html");
        response.getWriter().println("<h1>Framework Controller</h1>");
        response.getWriter().println("<p>Path: " + path + "</p>");
    }
    
    private boolean isStaticResource(String path) {
        return path.endsWith(".js") || path.endsWith(".css") || 
               path.endsWith(".html") || path.endsWith(".htm") ||
               path.endsWith(".png") || path.endsWith(".jpg") || 
               path.endsWith(".gif") || path.endsWith(".ico");
    }
}
