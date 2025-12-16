<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Employe</title>
</head>
<body>
    <h1 align="center">ETU003326</h1>
    <h1>Sprint 8bis - Binding d'objet depuis formulaire</h1>
    
    <form action="<%= request.getContextPath() %>/objet" method="POST">
        <div class="section">
            <h3>Informations de l'employe</h3>
            <label for="nom">Nom:</label>
            <input type="text" id="nom" name="e.nom" required>
            <br>
            
            <label for="prenom">Prenom:</label>
            <input type="text" id="prenom" name="e.prenom" required>
            <br>
            
            <label for="email">Email:</label>
            <input type="email" id="email" name="e.email" required>
            <br>
            
            <label for="salaire">Salaire:</label>
            <input type="number" id="salaire" name="e.salaire" step="0.01" required>
            <br>
            
            <label for="deptId">Departement ID:</label>
            <input type="number" id="deptId" name="e.departementId" required>
            <br>
        </div>
        
        <button type="submit">Enregistrer l'employe</button>
    </form>
    
    <div>
        <strong>Note:</strong> Les champs utilisent la notation <code>e.propriete</code> (ex: <code>e.nom</code>)<br>
        Le framework detecte automatiquement le paramètre <code>Employe e</code> et remplit ses proprietes.
    </div>
</body>
</html>
