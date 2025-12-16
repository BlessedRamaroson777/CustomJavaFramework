<html>
<head>
    <title>Résultat upload</title>
</head>
<body>
    <h1>Fichiers recus</h1>
    <ul>
    <% Object obj = request.getAttribute("files");
       if (obj instanceof java.util.List) {
           java.util.List<?> list = (java.util.List<?>) obj;
           for (Object f : list) { %>
               <li><%= f %></li>
    <%     }
       } else if (obj != null) { %>
           <li><%= obj %></li>
    <% } else { %>
           <li>Aucun fichier</li>
    <% } %>
    </ul>

    <h2>Chemins serveur (saved)</h2>
    <ul>
    <% Object up = request.getAttribute("uploadedFiles");
       if (up instanceof java.util.Map) {
           @SuppressWarnings("unchecked")
           java.util.Map<String, java.util.List<String>> m = (java.util.Map<String, java.util.List<String>>) up;
           for (String field : m.keySet()) {
               java.util.List<String> paths = m.get(field);
               if (paths != null) {
                   for (String p : paths) { %>
                       <li><%= field %> -> <%= p %></li>
    <%               }
               }
           }
       } else { %>
           <li>Aucun chemin serveur disponible</li>
    <% } %>
    </ul>
    <p><a href="upload">Retour</a></p>
</body>
</html>