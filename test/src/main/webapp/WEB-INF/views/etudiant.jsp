<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fiche Etudiant</title>
</head>
<body>
    <h1 align="center">ETU003326</h1>
    <h1>Fiche Etudiant (6)</h1>
    <p>Cette page est affichee via ModelView !</p>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <hr/>
    <h3>Debug binding (FrontController)</h3>
    <p>boundParams: <%= request.getAttribute("boundParams") != null ? request.getAttribute("boundParams") : "(none)" %></p>
    <p>attribute `id`: <%= request.getAttribute("id") != null ? request.getAttribute("id") : "(none)" %></p>
</body>
</html>
