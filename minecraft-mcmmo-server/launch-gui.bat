@echo off
echo 🎮 Starting MCMMO Server GUI Launcher...

:: Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java not found! Please install Java 21 from https://adoptium.net
    pause
    exit /b 1
)

:: Compile the GUI launcher
echo 🔨 Compiling GUI launcher...
javac MCMMOServerLauncher.java

if %errorlevel% equ 0 (
    echo ✅ Compilation successful!
    echo 🚀 Launching GUI...
    java MCMMOServerLauncher
) else (
    echo ❌ Compilation failed!
    pause
    exit /b 1
) 