<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fiche Etudiant (6 tier)</title>
</head>
<body>
    <h1>Fiche Etudiant (6 tier)</h1>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <hr/>
    <h2>Données du modèle</h2>
    <p>studentId: <%= request.getAttribute("studentId") != null ? request.getAttribute("studentId") : "(aucun)" %></p>
    <p>message: <%= request.getAttribute("message") != null ? request.getAttribute("message") : "(aucun)" %></p>
</body>
</html>
