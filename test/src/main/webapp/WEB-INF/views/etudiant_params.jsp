<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Etudiant - Request Params (6 bis)</title>
</head>
<body>
    <h1 align="center">ETU003326</h1>
    <h1>Etudiant - Request Params (6 bis)</h1>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <h3>Debug binding (FrontController)</h3>
    <p>boundParams: <%= request.getAttribute("boundParams") != null ? request.getAttribute("boundParams") : "(none)" %></p>
    <p>attribute `id`: <%= request.getAttribute("id") != null ? request.getAttribute("id") : "(none)" %></p>
    <p>attribute `name`: <%= request.getAttribute("name") != null ? request.getAttribute("name") : "(none)" %></p>
</body>
</html>
