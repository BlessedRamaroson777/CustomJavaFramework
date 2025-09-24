<%
    String message = (String) request.getAttribute("message");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Login</title>
    </head>
    <body>
        <form action="" method="post">
            <% if(message!=null) { %>
                <p>
                    <%= message %>
                </p>
            <% } %>
            <p><input type="text" name="nom" id="nom" placeholder="Votre nom"></p>
            <p><input type="password" name="password" id="password" placeholder="Votre mot de passe"></p>
            <p><input type="submit" value="Se connecter"></p>
        </form>
    </body>
</html>