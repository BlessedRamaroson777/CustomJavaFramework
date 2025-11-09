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
</body>
</html>
