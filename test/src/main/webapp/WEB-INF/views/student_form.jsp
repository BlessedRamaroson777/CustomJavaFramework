<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulaire Etudiant</title>
</head>
<body>
    <h1 align="center">ETU003326</h1>
    <h1>Formulaire Etudiant</h1>
    <p>Remplissez le formulaire ci-dessous. Les données seront collectées dans un Map&lt;String, Object&gt; et "sauvegardées" (simulation).</p>
    
    <form action="<%= request.getContextPath() %>/form/submit" method="POST">
        <div class="form-group">
            <label for="nom">Nom:</label>
            <input type="text" id="nom" name="nom" required>
        </div>
        
        <div class="form-group">
            <label for="prenom">Prenom:</label>
            <input type="text" id="prenom" name="prenom" required>
        </div>
        
        <button type="submit">Soumettre</button>
    </form>
    
    <hr/>
    <h3>Debug info</h3>
    <p>boundParams: <%= request.getAttribute("boundParams") != null ? request.getAttribute("boundParams") : "(none)" %></p>
</body>
</html>
