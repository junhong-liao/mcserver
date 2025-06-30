#!/bin/bash

echo "🎮 Starting Minecraft Server GUI..."

# Navigate to server directory
cd "$(dirname "$0")"

# Compile the GUI launcher
echo "🔨 Compiling GUI..."
javac MinecraftServerGUI.java

if [ $? -eq 0 ]; then
    echo "🚀 Launching GUI..."
    java MinecraftServerGUI
else
    echo "❌ Failed to compile GUI launcher"
    exit 1
fi 