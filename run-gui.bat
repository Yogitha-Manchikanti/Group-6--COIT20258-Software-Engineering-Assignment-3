@echo off
REM THS-Enhanced JavaFX GUI Launcher
REM This script runs the JavaFX application with all required dependencies

echo.
echo ========================================
echo  THS-Enhanced JavaFX GUI Launcher
echo ========================================
echo.

REM Set JavaFX version
set JAVAFX_VERSION=21.0.3

REM Set Maven repository path
set M2_REPO=%USERPROFILE%\.m2\repository

REM Build classpath
set CLASSPATH=target\classes
set CLASSPATH=%CLASSPATH%;mysql-connector-j-8.0.33.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-base\%JAVAFX_VERSION%\javafx-base-%JAVAFX_VERSION%-win.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%\javafx-controls-%JAVAFX_VERSION%-win.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-graphics\%JAVAFX_VERSION%\javafx-graphics-%JAVAFX_VERSION%-win.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%.jar
set CLASSPATH=%CLASSPATH%;%M2_REPO%\org\openjfx\javafx-fxml\%JAVAFX_VERSION%\javafx-fxml-%JAVAFX_VERSION%-win.jar

echo Checking dependencies...
if not exist "target\classes\com\mycompany\coit20258assignment2\App.class" (
    echo ERROR: Application not compiled. Please compile first.
    echo Run: javac or use your IDE to compile.
    pause
    exit /b 1
)

if not exist "%M2_REPO%\org\openjfx\javafx-controls\%JAVAFX_VERSION%" (
    echo ERROR: JavaFX libraries not found.
    echo Please install Maven and run: mvn dependency:resolve
    pause
    exit /b 1
)

echo.
echo Starting JavaFX Application...
echo Main Class: com.mycompany.coit20258assignment2.App
echo.
echo NOTE: Make sure the server is running in another window!
echo Server command: java -cp "target/classes;mysql-connector-j-8.0.33.jar" com.mycompany.coit20258assignment2.server.THSServer
echo.

REM Run the application
java -cp "%CLASSPATH%" com.mycompany.coit20258assignment2.App

echo.
echo Application closed.
pause
