<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmation - Employe Enregistre</title>
</head>
<body>
    <h1>✓ Employe Enregistre avec Succès</h1>
    
    <div class="success-box">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "Donnees enregistrees!" %>
    </div>
    
    <h2>Details de l'employe</h2>
    
    <% 
        Object employeObj = request.getAttribute("employe");
        if (employeObj != null && employeObj instanceof mg.itu.test.model.Employe) {
            mg.itu.test.model.Employe emp = (mg.itu.test.model.Employe) employeObj;
    %>
    <table>
        <thead>
            <tr>
                <th>Propriete</th>
                <th>Valeur</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><strong>Nom</strong></td>
                <td><%= emp.getNom() != null ? emp.getNom() : "(non defini)" %></td>
            </tr>
            <tr>
                <td><strong>Prenom</strong></td>
                <td><%= emp.getPrenom() != null ? emp.getPrenom() : "(non defini)" %></td>
            </tr>
            <tr>
                <td><strong>Email</strong></td>
                <td><%= emp.getEmail() != null ? emp.getEmail() : "(non defini)" %></td>
            </tr>
            <tr>
                <td><strong>Salaire</strong></td>
                <td><%= emp.getSalaire() %> €</td>
            </tr>
            <tr>
                <td><strong>Departement ID</strong></td>
                <td><%= emp.getDepartementId() %></td>
            </tr>
            <tr>
                <td><strong>toString()</strong></td>
                <td><%= emp.toString() %></td>
            </tr>
        </tbody>
    </table>
    <% } else { %>
        <p>Aucune donnee d'employe disponible.</p>
    <% } %>
    
    <hr/>
    <p><a href="<%= request.getContextPath() %>/objet">← Retour au formulaire</a></p>
</body>
</html>
