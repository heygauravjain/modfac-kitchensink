@echo off
setlocal enabledelayedexpansion

REM KitchenSink Docker Management Script for Windows

if "%1"=="" goto help

if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="logs" goto logs
if "%1"=="status" goto status
if "%1"=="clean" goto clean
if "%1"=="help" goto help
goto unknown

:start
echo ================================
echo Starting KitchenSink Application
echo ================================
echo.
echo Building and starting services...
docker-compose up --build -d
echo.
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul
echo.
echo Application is ready!
echo Access the application at: http://localhost:8080
echo Health check: http://localhost:8080/actuator/health
goto end

:stop
echo ================================
echo Stopping KitchenSink Application
echo ================================
echo.
echo Stopping services...
docker-compose down
echo.
echo Application stopped successfully!
goto end

:restart
echo ================================
echo Restarting KitchenSink Application
echo ================================
echo.
call :stop
call :start
goto end

:logs
echo ================================
echo Showing Application Logs
echo ================================
echo.
if "%2"=="follow" (
    echo Following logs (Ctrl+C to exit)...
    docker-compose logs -f
) else (
    docker-compose logs
)
goto end

:status
echo ================================
echo Application Status
echo ================================
echo.
docker-compose ps
goto end

:clean
echo ================================
echo Cleaning Up Docker Resources
echo ================================
echo.
echo This will remove all containers, volumes, and images for this project.
set /p confirm="Are you sure? (y/N): "
if /i "!confirm!"=="y" (
    echo Stopping and removing containers...
    docker-compose down -v
    echo.
    echo Removing images...
    for /f "tokens=*" %%i in ('docker images -q kitchensink-kitchensink-app 2^>nul') do docker rmi %%i 2>nul
    echo.
    echo Cleanup completed!
) else (
    echo Cleanup cancelled.
)
goto end

:help
echo ================================
echo KitchenSink Docker Management Script
echo ================================
echo.
echo Usage: %0 [COMMAND]
echo.
echo Commands:
echo   start     Build and start the application
echo   stop      Stop the application
echo   restart   Restart the application
echo   logs      Show application logs
echo   logs follow  Follow logs in real-time
echo   status    Show application status
echo   clean     Remove all containers, volumes, and images
echo   help      Show this help message
echo.
echo Examples:
echo   %0 start
echo   %0 logs follow
echo   %0 clean
goto end

:unknown
echo [ERROR] Unknown command: %1
echo.
goto help

:end 