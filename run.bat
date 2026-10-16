@echo off
echo Starting Online Examination System...
echo.

if not exist out\com\examportal\Main.class (
    echo ERROR: Project not compiled. Run build.bat first.
    pause
    exit /b 1
)

for /f %%i in ('dir /b lib\mysql-connector-j*.jar') do set MYSQL_JAR=lib\%%i
java -cp ".;out;%MYSQL_JAR%" com.examportal.Main
