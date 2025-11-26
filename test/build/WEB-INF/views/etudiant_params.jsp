<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Etudiant - Request Params (6 bis)</title>
</head>
<body>
    <h1>Etudiant - Request Params (6 bis)</h1>
    <p>URL demandee : <%= request.getRequestURI() %></p>
    <hr/>
    <h2>Request parameters</h2>
    <p>idParam: <%= request.getAttribute("idParam") != null ? request.getAttribute("idParam") : "(aucun)" %></p>
    <p>nameParam: <%= request.getAttribute("nameParam") != null ? request.getAttribute("nameParam") : "(aucun)" %></p>    <hr/>
    <h3>Debug binding (FrontController)</h3>
    <p>boundParams: <%= request.getAttribute("boundParams") != null ? request.getAttribute("boundParams") : "(none)" %></p>
    <p>attribute `id`: <%= request.getAttribute("id") != null ? request.getAttribute("id") : "(none)" %></p>
    <p>attribute `name`: <%= request.getAttribute("name") != null ? request.getAttribute("name") : "(none)" %></p>
</body>
</html>
