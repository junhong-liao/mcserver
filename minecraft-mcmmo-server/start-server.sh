#!/bin/bash
while true; do
    echo "Starting MCMMO server..."
    /opt/homebrew/opt/openjdk@21/bin/java -Xmx4G -Xms2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -jar paper.jar --nogui
    echo "Server stopped. Restarting in 5 seconds... (Ctrl+C to exit)"
    sleep 5
done
