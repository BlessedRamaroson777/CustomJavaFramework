@echo off
set APP_NAME=TestFramework
set SRC_DIR=src\main\java
set WEB_DIR=src\main\webapp
set BUILD_DIR=build
set LIB_DIR=..\lib
set TOMCAT_WEBAPPS=C:\xampp\tomcat\webapps
set SERVLET_API_JA`R=%LIB_DIR%\servlet-api.jar
set FRAMEWORK_JAR=%LIB_DIR%\framework.jar

REM Suppression et recreation du dossier build
if exist %BUILD_DIR% rmdir /s /q %BUILD_DIR%
mkdir %BUILD_DIR%\WEB-INF\classes
mkdir %BUILD_DIR%\WEB-INF\lib

REM Compilation des fichiers Java avec les JARs necessaires
dir /s /b %SRC_DIR%\*.java > sources.txt
javac -cp "%SERVLET_API_JAR%;%FRAMEWORK_JAR%" -d %BUILD_DIR%\WEB-INF\classes @sources.txt
del sources.txt

REM Copier les fichiers web
xcopy %WEB_DIR% %BUILD_DIR% /E /I /Y

REM Copier le framework.jar dans WEB-INF/lib
copy %FRAMEWORK_JAR% %BUILD_DIR%\WEB-INF\lib\

REM Creation du fichier .war
cd %BUILD_DIR%
jar -cvf %APP_NAME%.war *
cd ..

REM Deploiement vers Tomcat
copy %BUILD_DIR%\%APP_NAME%.war %TOMCAT_WEBAPPS%

echo Deploiement termine. Redemarrez Tomcat si necessaire.
