<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Multiple - Employes</title>
</head>
<body>
    <h1>Sprint 8bis - Binding multiple objets et tableaux</h1>
    
    <form action="<%= request.getContextPath() %>/objet/multi" method="POST">
        <div class="section">
            <h3>Employe 1 (es[0])</h3>
            <label>Nom:</label>
            <input type="text" name="es[0].nom" required>
            
            <label>Prenom:</label>
            <input type="text" name="es[0].prenom" required>
            
            <label>Email:</label>
            <input type="email" name="es[0].email" required>
            
            <label>Salaire:</label>
            <input type="number" name="es[0].salaire" step="0.01" required>
            
            <label>Departement ID:</label>
            <input type="number" name="es[0].departementId" required>
        </div>
        
        <div class="section">
            <h3>Employe 2 (es[1])</h3>
            <label>Nom:</label>
            <input type="text" name="es[1].nom" required>
            
            <label>Prenom:</label>
            <input type="text" name="es[1].prenom" required>
            
            <label>Email:</label>
            <input type="email" name="es[1].email" required>
            
            <label>Salaire:</label>
            <input type="number" name="es[1].salaire" step="0.01" required>
            
            <label>Departement ID:</label>
            <input type="number" name="es[1].departementId" required>
        </div>
        
        <div class="section dept-section">
            <h3>Departement (d)</h3>
            <label>ID:</label>
            <input type="number" name="d.id" required>
            
            <label>Nom:</label>
            <input type="text" name="d.nom" required>
            
            <label>Localisation:</label>
            <input type="text" name="d.localisation" required>
        </div>
        
        <div class="section">
            <h3>Departement ID (deptId - simple int)</h3>
            <label>Department ID:</label>
            <input type="number" name="deptId" required>
        </div>
        
        <button type="submit">Enregistrer tout</button>
    </form>
    
    <div class="note">
        <strong>Note technique:</strong><br>
        • Tableau: <code>es[0].nom</code>, <code>es[1].nom</code> → <code>Employe[] es</code><br>
        • Objet: <code>d.nom</code>, <code>d.localisation</code> → <code>Departement d</code><br>
        • Primitif: <code>deptId</code> → <code>int deptId</code><br>
        Le framework detecte et remplit automatiquement chaque type!
    </div>
</body>
</html>
