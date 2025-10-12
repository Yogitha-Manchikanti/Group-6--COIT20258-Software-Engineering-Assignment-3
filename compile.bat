@echo off
echo Compiling THS-Enhanced Server Components...
echo ==========================================

REM Create output directory
if not exist "target\classes" mkdir target\classes

REM Set classpath for MySQL driver (you'll need to download it manually)
set MYSQL_JAR=mysql-connector-java-8.0.33.jar

echo.
echo Note: You need to download the MySQL JDBC driver:
echo https://dev.mysql.com/downloads/connector/j/
echo Place mysql-connector-java-8.0.33.jar in the project root directory
echo.

REM Compile Java files
echo Compiling Java source files...
javac -d target\classes -sourcepath src\main\java src\main\java\com\mycompany\coit20258assignment2\*.java src\main\java\com\mycompany\coit20258assignment2\common\*.java src\main\java\com\mycompany\coit20258assignment2\server\*.java src\main\java\com\mycompany\coit20258assignment2\server\dao\*.java src\main\java\module-info.java

if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful!
    echo.
    echo To run the server test:
    echo java -cp target\classes;%MYSQL_JAR% com.mycompany.coit20258assignment2.server.ServerTest
    echo.
    echo To run the server:
    echo java -cp target\classes;%MYSQL_JAR% com.mycompany.coit20258assignment2.server.THSServer
) else (
    echo ❌ Compilation failed!
)

pause