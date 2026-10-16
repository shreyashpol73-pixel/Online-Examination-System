@echo off
echo ================================
echo  Online Examination System
echo  Build Script
echo ================================
echo.

if not exist lib\mysql-connector-j*.jar (
    echo ERROR: MySQL Connector JAR not found in lib/ folder.
    echo Please download mysql-connector-j-8.x.jar and place it in the lib/ directory.
    echo Download from: https://dev.mysql.com/downloads/connector/j/
    pause
    exit /b 1
)

echo Creating output directory...
if not exist out mkdir out

echo Compiling Java sources...
for /f %%i in ('dir /b lib\mysql-connector-j*.jar') do set MYSQL_JAR=lib\%%i
javac -cp ".;%MYSQL_JAR%" -d out -sourcepath src src/com/examportal/Main.java src/com/examportal/db/*.java src/com/examportal/model/*.java src/com/examportal/dao/*.java src/com/examportal/ui/*.java

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED!
    pause
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL!
echo.
echo To run: java -cp ".;out;%MYSQL_JAR%" com.examportal.Main
pause
