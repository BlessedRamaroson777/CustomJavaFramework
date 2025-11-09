<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Erreur 404</title>
</head>
<body>
    <h1>404</h1>
    <h2>Page non trouvee</h2>
    <p><strong>Message :</strong> <%= exception != null ? exception.getMessage() : "Pas de methode pour cette URL" %></p>
    <p>URL demandee : <%= request.getAttribute("javax.servlet.error.request_uri") %></p>
</body>
</html>
