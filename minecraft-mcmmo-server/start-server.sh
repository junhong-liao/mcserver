#!/bin/bash
# Ensure we use Java 21 for Paper 1.21.6
export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null) || export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
JAVA_CMD="${JAVA_HOME}/bin/java"

# Fallback to system java if Java 21 not found
if [ ! -f "$JAVA_CMD" ]; then
    JAVA_CMD="/opt/homebrew/opt/openjdk@21/bin/java"
    if [ ! -f "$JAVA_CMD" ]; then
        JAVA_CMD="java"
        echo "Warning: Using system Java. Paper 1.21.6 requires Java 21+"
    fi
fi

while true; do
    echo "Starting MCMMO server with Java 21..."
    "$JAVA_CMD" -Xmx4G -Xms2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -jar paper.jar --nogui
    echo "Server stopped. Restarting in 5 seconds... (Ctrl+C to exit)"
    sleep 5
done
