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
    <%
        java.util.Map bs = (java.util.Map) request.getAttribute("bindingSources");
        String idSource = "(inconnu)";
        String nameSource = "(inconnu)";
        if (bs != null) {
            Object o = bs.get("id"); if (o != null) idSource = o.toString();
            Object o2 = bs.get("name"); if (o2 != null) nameSource = o2.toString();
        }
    %>
</body>
</html>
