<html>
<head>
    <title>Upload de fichiers</title>
</head>
<body>
    <h1>Formulaire d'upload</h1>
    <form action="upload" method="post" enctype="multipart/form-data">
        <label>Fichiers :</label>
        <input type="file" name="files" multiple />
        <br/><br/>
        <button type="submit">Envoyer</button>
    </form>
    <p><a href="/">Retour</a></p>
</body>
</html>