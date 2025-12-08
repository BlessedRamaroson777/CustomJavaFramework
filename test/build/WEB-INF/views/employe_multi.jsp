<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Multiple - Employés</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #333; }
        form { background: #f9f9f9; padding: 20px; border-radius: 8px; }
        .section { background: #fff; padding: 15px; margin-top: 15px; border-left: 4px solid #4CAF50; }
        .dept-section { border-left-color: #2196F3; }
        label { display: block; margin-top: 10px; font-weight: bold; color: #555; }
        input { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        button { margin-top: 20px; padding: 10px 20px; background: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        button:hover { background: #45a049; }
        .note { margin-top: 20px; padding: 15px; background: #e7f3ff; border-left: 4px solid #2196F3; }
    </style>
</head>
<body>
    <h1>Sprint 8bis - Binding multiple objets et tableaux</h1>
    
    <form action="<%= request.getContextPath() %>/objet/multi" method="POST">
        <div class="section">
            <h3>Employé 1 (es[0])</h3>
            <label>Nom:</label>
            <input type="text" name="es[0].nom" required>
            
            <label>Prénom:</label>
            <input type="text" name="es[0].prenom" required>
            
            <label>Email:</label>
            <input type="email" name="es[0].email" required>
            
            <label>Salaire:</label>
            <input type="number" name="es[0].salaire" step="0.01" required>
            
            <label>Département ID:</label>
            <input type="number" name="es[0].departementId" required>
        </div>
        
        <div class="section">
            <h3>Employé 2 (es[1])</h3>
            <label>Nom:</label>
            <input type="text" name="es[1].nom" required>
            
            <label>Prénom:</label>
            <input type="text" name="es[1].prenom" required>
            
            <label>Email:</label>
            <input type="email" name="es[1].email" required>
            
            <label>Salaire:</label>
            <input type="number" name="es[1].salaire" step="0.01" required>
            
            <label>Département ID:</label>
            <input type="number" name="es[1].departementId" required>
        </div>
        
        <div class="section dept-section">
            <h3>Département (d)</h3>
            <label>ID:</label>
            <input type="number" name="d.id" required>
            
            <label>Nom:</label>
            <input type="text" name="d.nom" required>
            
            <label>Localisation:</label>
            <input type="text" name="d.localisation" required>
        </div>
        
        <div class="section">
            <h3>Département ID (deptId - simple int)</h3>
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
        Le framework détecte et remplit automatiquement chaque type!
    </div>
</body>
</html>
