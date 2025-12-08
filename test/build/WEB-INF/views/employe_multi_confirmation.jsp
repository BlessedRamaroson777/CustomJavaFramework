<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmation - Données Multiples</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #4CAF50; }
        .success-box { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; border-radius: 5px; margin: 20px 0; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #4CAF50; color: white; }
        tr:hover { background-color: #f5f5f5; }
        .section { margin: 30px 0; }
        .dept-table th { background-color: #2196F3; }
        a { color: #4CAF50; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>✓ Données Enregistrées avec Succès</h1>
    
    <div class="success-box">
        <%= request.getAttribute("message") != null ? request.getAttribute("message") : "Données enregistrées!" %>
    </div>
    
    <div class="section">
        <h2>Employés (Tableau Employe[])</h2>
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
                    <th>Prénom</th>
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
            <p>Aucun employé.</p>
        <% } %>
    </div>
    
    <div class="section">
        <h2>Département (Objet Departement)</h2>
        <% 
            Object deptObj = request.getAttribute("departement");
            if (deptObj != null && deptObj instanceof mg.itu.test.model.Departement) {
                mg.itu.test.model.Departement dept = (mg.itu.test.model.Departement) deptObj;
        %>
        <table class="dept-table">
            <thead>
                <tr>
                    <th>Propriété</th>
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
            <p>Aucun département.</p>
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
