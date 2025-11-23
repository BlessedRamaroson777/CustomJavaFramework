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
    <p>nameParam: <%= request.getAttribute("nameParam") != null ? request.getAttribute("nameParam") : "(aucun)" %></p>
    <p>Note: <%= request.getAttribute("note") %></p>
</body>
</html>
