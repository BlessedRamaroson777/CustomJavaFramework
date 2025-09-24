@echo off
echo ===== Compilation du Framework =====
cd framework
call build.bat
cd ..

echo.
echo ===== Compilation du Test =====
cd test
call build.bat
cd ..

echo.
echo ===== Compilation terminee =====
