<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmation - Formulaire Soumis</title>
</head>
<body>
    <h1>Formulaire Soumis avec Succès</h1>
    
    <div class="success-box">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "Données enregistrées!" %>
    </div>
    
    <h2>Données collectées et sauvegardées (Map&lt;String, Object&gt;)</h2>
    
    <% 
        // Le framework expose automatiquement formData depuis request.getParameterMap()
        Object formDataObj = request.getAttribute("formData");
        if (formDataObj instanceof java.util.Map) {
            java.util.Map<?, ?> formData = (java.util.Map<?, ?>) formDataObj;
    %>
    <table>
        <thead>
            <tr>
                <th>Champ</th>
                <th>Valeur</th>
                <th>Type</th>
            </tr>
        </thead>
        <tbody>
            <% for (java.util.Map.Entry<?, ?> entry : formData.entrySet()) { 
                String typeName = entry.getValue() != null ? entry.getValue().getClass().getSimpleName() : "null";
            %>
            <tr>
                <td><strong><%= entry.getKey() %></strong></td>
                <td><%= entry.getValue() != null ? entry.getValue() : "(null)" %></td>
                <td><em><%= typeName %></em></td>
            </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
        <p>Aucune donnée disponible.</p>
    <% } %>
    
    <hr/>
    <h3>Console log (vérifiez la console du serveur)</h3>
    <p>Les données ont été affichées dans la console avec <code>System.out.println()</code> simulant une opération "save".</p>
    
    <hr/>
    <h3>Debug info (FrontController)</h3>
    <p>boundParams: <%= request.getAttribute("boundParams") != null ? request.getAttribute("boundParams") : "(none)" %></p>
    <p>attribute `nom`: <%= request.getAttribute("nom") != null ? request.getAttribute("nom") : "(none)" %></p>
    <p>attribute `prenom`: <%= request.getAttribute("prenom") != null ? request.getAttribute("prenom") : "(none)" %></p>
    
    <hr/>
    <p><a href="<%= request.getContextPath() %>/form">← Retour au formulaire</a></p>
</body>
</html>
