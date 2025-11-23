<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fiche Etudiant</title>
</head>
<body>
    <h1>Fiche Etudiant (6)</h1>
    <p>Cette page est affichee via ModelView !</p>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <hr/>
    <h2>Données du modèle</h2>
    <% 
        Object mapObj = request.getAttribute("mapData");
        if (mapObj instanceof java.util.Map) {
            java.util.Map<?,?> m = (java.util.Map<?,?>) mapObj;
    %>
        <ul>
        <% for (java.util.Map.Entry<?,?> entry : m.entrySet()) { %>
            <li><strong><%= entry.getKey() %></strong> : <%= entry.getValue() %></li>
        <% } %>
        </ul>
    <% } else { %>
        <p>Aucune donnée de modèle disponible.</p>
    <% } %>
</body>
</html>
