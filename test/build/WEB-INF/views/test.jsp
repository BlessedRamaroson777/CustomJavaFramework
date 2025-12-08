<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Page Test</title>
</head>
<body>
    <h1>Test Page</h1>
    <p>Cette page est affichee via ModelView !</p>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <hr/>
    <h2>Données du modèle (toutes les paires clé / valeur)</h2>
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
    <h2>Update student</h2>
    <form action="/TestFramework/student/update" method="post">
        <label for="name">Name:</label>
        <input type="text" id="name" name="name" />
        <br/>
        <button type="submit">Save</button>
    </form>
</body>
</html>
