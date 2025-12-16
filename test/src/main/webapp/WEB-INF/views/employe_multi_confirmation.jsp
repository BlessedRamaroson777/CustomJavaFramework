<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmation - Donnees Multiples</title>
</head>
<body>
    <h1 align="center">ETU003326</h1>
    <h1>✓ Donnees Enregistrees avec Succes</h1>
    
    <div class="success-box">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "Donnees enregistrees!" %>
    </div>
    
    <div class="section">
        <h2>Employes (Tableau Employe[])</h2>
        <% 
            Object employesObj = request.getAttribute("employes");
            if (employesObj != null && employesObj instanceof mg.itu.test.model.Employe[]) {
                mg.itu.test.model.Employe[] employes = (mg.itu.test.model.Employe[]) employesObj;
        %>
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Nom</th>
                    <th>Prenom</th>
                    <th>Email</th>
                    <th>Salaire</th>
                    <th>Dept ID</th>
                </tr>
            </thead>
            <tbody>
                <% for (int i = 0; i < employes.length; i++) { 
                    mg.itu.test.model.Employe emp = employes[i];
                    if (emp != null) {
                %>
                <tr>
                    <td><strong><%= i %></strong></td>
                    <td><%= emp.getNom() %></td>
                    <td><%= emp.getPrenom() %></td>
                    <td><%= emp.getEmail() %></td>
                    <td><%= emp.getSalaire() %> €</td>
                    <td><%= emp.getDepartementId() %></td>
                </tr>
                <% 
                    }
                } %>
            </tbody>
        </table>
        <% } else { %>
            <p>Aucun employe.</p>
        <% } %>
    </div>
    
    <div class="section">
        <h2>Departement (Objet Departement)</h2>
        <% 
            Object deptObj = request.getAttribute("departement");
            if (deptObj != null && deptObj instanceof mg.itu.test.model.Departement) {
                mg.itu.test.model.Departement dept = (mg.itu.test.model.Departement) deptObj;
        %>
        <table class="dept-table">
            <thead>
                <tr>
                    <th>Propriete</th>
                    <th>Valeur</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>ID</strong></td>
                    <td><%= dept.getId() %></td>
                </tr>
                <tr>
                    <td><strong>Nom</strong></td>
                    <td><%= dept.getNom() %></td>
                </tr>
                <tr>
                    <td><strong>Localisation</strong></td>
                    <td><%= dept.getLocalisation() %></td>
                </tr>
                <tr>
                    <td><strong>toString()</strong></td>
                    <td><%= dept.toString() %></td>
                </tr>
            </tbody>
        </table>
        <% } else { %>
            <p>Aucun departement.</p>
        <% } %>
    </div>
    
    <div class="section">
        <h2>DeptId (Primitif int)</h2>
        <p><strong>Valeur:</strong> <%= request.getAttribute("deptId") %></p>
    </div>
    
    <hr/>
    <p><a href="<%= request.getContextPath() %>/objet/multi">← Retour au formulaire</a></p>
</body>
</html>
