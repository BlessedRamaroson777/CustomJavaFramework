<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Erreur 404</title>
    <style>body{font-family:Arial,Helvetica,sans-serif;margin:24px;} .hint{color:#a00}</style>
</head>
<body>
    <h1>Erreur <%= request.getAttribute("javax.servlet.error.status_code") %></h1>
    <h2>Page non trouvee / Route introuvable</h2>
    <%
        String errMsg = (String) request.getAttribute("javax.servlet.error.message");
        if (errMsg == null && exception != null) errMsg = exception.getMessage();
        if (errMsg == null) errMsg = "Aucune information disponible.";

        String hint = null;
        if (errMsg.contains("Page JSP introuvable")) {
            hint = "La vue spécifiée est introuvable dans /WEB-INF/views/. Vérifiez le nom fourni par le contrôleur.";
        } else if (errMsg.contains("Methode non supportee")) {
            hint = "Le handler retourne un type non supporté (attendu ModelView ou String).";
        } else if (errMsg.contains("Pas de methode pour cette URL")) {
            hint = "Aucune méthode correspondant à cette URL n'a été trouvée. Vérifiez les annotations @MyURL sur vos controlleurs.";
        }
    %>
    <p><strong>Message :</strong> <%= errMsg %></p>
    <p><strong>URL demandée :</strong> <%= request.getAttribute("javax.servlet.error.request_uri") %></p>
    <% if (hint != null) { %>
        <p class="hint"><strong>Indice :</strong> <%= hint %></p>
    <% } %>
</body>
</html>
