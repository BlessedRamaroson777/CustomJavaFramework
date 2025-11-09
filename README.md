# Framework MrNaina - Sprint 2

## Architecture

Le projet est divise en deux parties :

### 1. Framework (`framework/`)
Contient les annotations et le servlet principal :
- **Annotations** : `@MyController`, `@MyURL`
- **Scanner** : `ControllerScanner` pour detecter les classes annotees
- **Servlet** : `FrontController` qui scanne au demarrage et route les requêtes

### 2. Test (`test/`)
Application de test qui utilise le framework :
- **Contrôleurs** : Classes annotees avec `@MyController`
- **Methodes** : Annotees avec `@MyURL("/path")`

## Comment ça marche

1. **Au demarrage de Tomcat** :
   - `FrontController` lit le paramètre `basePackage` depuis `web.xml`
   - Il scanne toutes les classes du package pour trouver celles annotees `@MyController`
   - Pour chaque classe, il scanne les methodes annotees `@MyURL`
   - Il stocke une map URL → (Classe, Methode) dans le `ServletContext`

2. **Quand une requête arrive** :
   - `FrontController` cherche l'URL dans la map
   - **Si trouvee** : affiche les infos (classe et methode) sans invoquer
   - **Si non trouvee** : affiche "Pas de methode pour cette URL" + liste des routes disponibles

## Build & Deploiement

### Compiler le framework
```batch
cd framework
build.bat
```

### Compiler et deployer le test
```batch
cd test
build.bat
```

Le WAR sera automatiquement copie dans `C:\xampp\tomcat\webapps\`

## Tester

Redemarrez Tomcat, puis testez ces URLs :

- `http://localhost:8080/TestFramework/` → Pas de methode (liste les routes)
- `http://localhost:8080/TestFramework/index` → Trouve TestController.index
- `http://localhost:8080/TestFramework/test` → Trouve TestController.test
- `http://localhost:8080/TestFramework/hello` → Trouve TestController.hello
- `http://localhost:8080/TestFramework/autre` → Pas de methode pour cette URL

## Configuration

Dans `test/src/main/webapp/WEB-INF/web.xml` :

```xml
<init-param>
    <param-name>basePackage</param-name>
    <param-value>mg.itu.test.controller</param-value>
</init-param>
```

Change la valeur pour scanner un autre package.

## Exemple de contrôleur

```java
package mg.itu.test.controller;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;

@MyController("MonController")
public class MonController {
    
    @MyURL("/maroute")
    public String maMethode() {
        return "Resultat de ma methode";
    }
}
```
