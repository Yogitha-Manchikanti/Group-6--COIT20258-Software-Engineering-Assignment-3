# ========================================
# THS-Enhanced Build Script
# ========================================
# This script builds executable JAR files for the THS-Enhanced system
# Creates both Client and Server JAR files with all dependencies included
#
# Usage: .\build-executables.ps1
# ========================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  THS-Enhanced Build Script" -ForegroundColor Cyan
Write-Host "  Building Executable JAR Files..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Clean previous builds
Write-Host "[Step 1/4] Cleaning previous builds..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Clean failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Clean completed successfully" -ForegroundColor Green
Write-Host ""

# Compile the project
Write-Host "[Step 2/4] Compiling source code..." -ForegroundColor Yellow
mvn compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Compilation completed successfully" -ForegroundColor Green
Write-Host ""

# Package into executable JARs
Write-Host "[Step 3/4] Packaging executable JARs..." -ForegroundColor Yellow
mvn package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Packaging failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Packaging completed successfully" -ForegroundColor Green
Write-Host ""

# Create executables directory
Write-Host "[Step 4/4] Organizing executable files..." -ForegroundColor Yellow
$execDir = ".\executable"
if (Test-Path $execDir) {
    Remove-Item $execDir -Recurse -Force
}
New-Item -ItemType Directory -Path $execDir | Out-Null

# Copy JAR files
Copy-Item ".\target\THS-Enhanced-Client.jar" -Destination "$execDir\THS-Enhanced-Client.jar"
Copy-Item ".\target\THS-Enhanced-Server.jar" -Destination "$execDir\THS-Enhanced-Server.jar"

# Create run scripts
$clientScript = @"
@echo off
echo ========================================
echo  THS-Enhanced Client Application
echo ========================================
echo Starting client...
echo.
java -jar THS-Enhanced-Client.jar
pause
"@
Set-Content -Path "$execDir\run-client.bat" -Value $clientScript

$serverScript = @"
@echo off
echo ========================================
echo  THS-Enhanced Server Application
echo ========================================
echo Starting server on port 8080...
echo.
java -jar THS-Enhanced-Server.jar
pause
"@
Set-Content -Path "$execDir\run-server.bat" -Value $serverScript

# Create README
$readmeContent = @"
# THS-Enhanced Executable Files

## 📦 Contents

- **THS-Enhanced-Client.jar** - Client application (JavaFX GUI)
- **THS-Enhanced-Server.jar** - Server application (TCP Server)
- **run-client.bat** - Windows batch script to run client
- **run-server.bat** - Windows batch script to run server

## 🚀 How to Run

### Prerequisites
- Java 17 or higher installed
- MySQL Server running (for server mode)
- Database setup completed (run setup_database.sql)

### Option 1: Using Batch Scripts (Windows)

**Start Server:**
1. Double-click ``run-server.bat``
2. Wait for "THS Server is running on port 8080" message

**Start Client:**
1. Double-click ``run-client.bat``
2. Login screen will appear

### Option 2: Using Command Line

**Start Server:**
``````
java -jar THS-Enhanced-Server.jar
``````

**Start Client:**
``````
java -jar THS-Enhanced-Client.jar
``````

### Option 3: Using Java Command (macOS/Linux)

**Start Server:**
``````bash
java -jar THS-Enhanced-Server.jar
``````

**Start Client:**
``````bash
java -jar THS-Enhanced-Client.jar
``````

## 👥 Test Accounts

### Administrator
- Username: ``admin``
- Password: ``admin123``

### Doctor
- Username: ``drjohnson``
- Password: ``doctor123``

### Patient
- Username: ``jsmith``
- Password: ``patient123``

## 🔧 Troubleshooting

**"Java not recognized" error:**
- Install Java 17 from: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- Add Java to PATH

**Server connection error:**
- Ensure MySQL server is running
- Check database credentials in DatabaseManager.java
- Verify server is running before starting client

**Port 8080 already in use:**
- Close any application using port 8080
- Or modify port in THSServer.java and rebuild

## 📚 Documentation

For complete documentation, see the main README.md in the project root.

---
**THS-Enhanced v3.0**
COIT20258 Software Engineering - Group 6
October 2025
"@
Set-Content -Path "$execDir\README.txt" -Value $readmeContent

Write-Host "✅ Executable files organized in 'executable' folder" -ForegroundColor Green
Write-Host ""

# Display summary
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Build Completed Successfully! ✅" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📦 Executable files created:" -ForegroundColor Cyan
Write-Host "   • executable\THS-Enhanced-Client.jar" -ForegroundColor White
Write-Host "   • executable\THS-Enhanced-Server.jar" -ForegroundColor White
Write-Host "   • executable\run-client.bat" -ForegroundColor White
Write-Host "   • executable\run-server.bat" -ForegroundColor White
Write-Host "   • executable\README.txt" -ForegroundColor White
Write-Host ""
Write-Host "🚀 To run the application:" -ForegroundColor Cyan
Write-Host "   1. Start server: cd executable; .\run-server.bat" -ForegroundColor White
Write-Host "   2. Start client: cd executable; .\run-client.bat" -ForegroundColor White
Write-Host ""
Write-Host "📊 JAR file sizes:" -ForegroundColor Cyan
Get-ChildItem "$execDir\*.jar" | ForEach-Object {
    $sizeMB = [math]::Round($_.Length / 1MB, 2)
    Write-Host "   • $($_.Name): $sizeMB MB" -ForegroundColor White
}
Write-Host ""
$execPath = (Resolve-Path $execDir).Path
Write-Host "Location: $execPath" -ForegroundColor Yellow
Write-Host ""
