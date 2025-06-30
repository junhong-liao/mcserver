#!/bin/bash

echo "🎮 Starting MCMMO Server GUI Launcher..."

# Set Java 21 path
export JAVA_HOME="/opt/homebrew/Cellar/openjdk@21/21.0.7/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Check if Java 21 is available
if ! java -version 2>&1 | grep -q "21\."; then
    echo "⚠️  Java 21 not found. Installing with Homebrew..."
    brew install openjdk@21
    
    # Update symlinks
    sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
fi

# Compile the GUI launcher
echo "🔨 Compiling GUI launcher..."
javac MCMMOServerLauncher.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo "🚀 Launching GUI..."
    java MCMMOServerLauncher
else
    echo "❌ Compilation failed!"
    exit 1
fi 