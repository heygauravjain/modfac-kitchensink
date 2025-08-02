@echo off
setlocal enabledelayedexpansion

echo ========================================
echo 🛠️  KitchenSink Development Tools
echo ========================================
echo.
echo Choose an option:
echo 1. 🔨 Build and Compile
echo 2. 🧪 Run All Tests
echo 3. 🚀 Run Application
echo 4. 🐳 Docker Operations
echo 5. 📊 Test Coverage
echo 6. 🧹 Clean Project
echo 7. 🔍 Quick Health Check
echo 8. 📦 Package Application
echo 9. 🚪 Exit
echo.

set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto build
if "%choice%"=="2" goto test
if "%choice%"=="3" goto run
if "%choice%"=="4" goto docker
if "%choice%"=="5" goto coverage
if "%choice%"=="6" goto clean
if "%choice%"=="7" goto health
if "%choice%"=="8" goto package
if "%choice%"=="9" goto exit
goto invalid

:build
echo.
echo 🔨 Building and compiling project...
call mvnw.cmd clean compile
if %ERRORLEVEL% EQU 0 (
    echo ✅ Build successful!
) else (
    echo ❌ Build failed!
)
goto end

:test
echo.
echo 🧪 Running all tests...
call mvnw.cmd test
if %ERRORLEVEL% EQU 0 (
    echo ✅ All tests passed!
) else (
    echo ❌ Some tests failed!
)
goto end

:run
echo.
echo 🚀 Starting application...
call mvnw.cmd spring-boot:run
goto end

:docker
echo.
echo 🐳 Docker Operations
echo 1. Start all services
echo 2. Stop all services
echo 3. Rebuild and start
echo 4. View logs
echo 5. Back to main menu
echo.
set /p docker_choice="Enter choice (1-5): "

if "%docker_choice%"=="1" (
    echo Starting Docker services...
    docker-compose up -d
) else if "%docker_choice%"=="2" (
    echo Stopping Docker services...
    docker-compose down
) else if "%docker_choice%"=="3" (
    echo Rebuilding and starting Docker services...
    docker-compose up --build -d
) else if "%docker_choice%"=="4" (
    echo Viewing Docker logs...
    docker-compose logs -f
) else if "%docker_choice%"=="5" (
    goto main
) else (
    echo Invalid choice!
)
goto end

:coverage
echo.
echo 📊 Generating test coverage report...
call mvnw.cmd test jacoco:report
if %ERRORLEVEL% EQU 0 (
    echo ✅ Coverage report generated!
    echo 📍 Report location: target/site/jacoco/index.html
) else (
    echo ❌ Coverage report generation failed!
)
goto end

:clean
echo.
echo 🧹 Cleaning project...
call mvnw.cmd clean
if %ERRORLEVEL% EQU 0 (
    echo ✅ Project cleaned successfully!
) else (
    echo ❌ Clean failed!
)
goto end

:health
echo.
echo 🔍 Quick Health Check...
echo.
echo 📋 Checking Java version...
java -version 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Java is not available
) else (
    echo ✅ Java is available
)

echo.
echo 📋 Checking Maven wrapper...
call mvnw.cmd --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Maven wrapper is not working
) else (
    echo ✅ Maven wrapper is working
)

echo.
echo 📋 Checking project structure...
if exist "pom.xml" (
    echo ✅ pom.xml found
) else (
    echo ❌ pom.xml not found
)

if exist "src\main\java" (
    echo ✅ Source directory found
) else (
    echo ❌ Source directory not found
)

echo.
echo 📋 Testing compilation...
call mvnw.cmd compile -q
if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful
) else (
    echo ❌ Compilation failed
)

echo.
echo 📋 Testing basic test...
call mvnw.cmd test -Dtest=SimpleTest -q
if %ERRORLEVEL% EQU 0 (
    echo ✅ Basic test passed
) else (
    echo ❌ Basic test failed
)

echo.
echo 🎉 Health check completed!
goto end

:package
echo.
echo 📦 Packaging application...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% EQU 0 (
    echo ✅ Application packaged successfully!
    echo 📍 JAR location: target/kitchensink-0.0.1-SNAPSHOT.jar
) else (
    echo ❌ Packaging failed!
)
goto end

:invalid
echo.
echo ❌ Invalid choice! Please enter a number between 1-9.
goto end

:main
echo.
echo ========================================
echo 🛠️  KitchenSink Development Tools
echo ========================================
echo.
echo Choose an option:
echo 1. 🔨 Build and Compile
echo 2. 🧪 Run All Tests
echo 3. 🚀 Run Application
echo 4. 🐳 Docker Operations
echo 5. 📊 Test Coverage
echo 6. 🧹 Clean Project
echo 7. 🔍 Quick Health Check
echo 8. 📦 Package Application
echo 9. 🚪 Exit
echo.
set /p choice="Enter your choice (1-9): "
goto check_choice

:check_choice
if "%choice%"=="1" goto build
if "%choice%"=="2" goto test
if "%choice%"=="3" goto run
if "%choice%"=="4" goto docker
if "%choice%"=="5" goto coverage
if "%choice%"=="6" goto clean
if "%choice%"=="7" goto health
if "%choice%"=="8" goto package
if "%choice%"=="9" goto exit
goto invalid

:end
echo.
echo ========================================
echo Press any key to return to main menu...
pause >nul
goto main

:exit
echo.
echo 👋 Goodbye!
echo ========================================
exit /b 0 