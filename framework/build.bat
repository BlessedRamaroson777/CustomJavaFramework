@echo off
set FRAMEWORK_NAME=framework
set SRC_DIR=src\main\java
set BUILD_DIR=build
set LIB_DIR=..\lib
set SERVLET_API_JAR=%LIB_DIR%\servlet-api.jar

REM Suppression et recreation du dossier build
if exist %BUILD_DIR% rmdir /s /q %BUILD_DIR%
mkdir %BUILD_DIR%

REM Compilation des fichiers Java
dir /s /b %SRC_DIR%\*.java > sources.txt
javac -cp %SERVLET_API_JAR% -d %BUILD_DIR% @sources.txt
del sources.txt

REM Creation du fichier .jar
cd %BUILD_DIR%
jar -cvf %FRAMEWORK_NAME%.jar *
cd ..

REM Copier le jar dans le dossier lib du projet parent
copy %BUILD_DIR%\%FRAMEWORK_NAME%.jar %LIB_DIR%\

echo Framework compile en .jar avec succes.
