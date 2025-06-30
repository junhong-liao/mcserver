#!/bin/bash
echo "================================"
echo " MCMMO Single-Player Auto-Setup"
echo "================================"
echo ""
echo "⚠️  REQUIREMENT: Java 21+ is required for Paper 1.21.6"
echo "📥 Install Java 21: brew install openjdk@21"
echo ""

# Create server directory
echo "[1/6] Creating server directory..."
mkdir -p minecraft-mcmmo-server
cd minecraft-mcmmo-server

# Download Paper server (latest 1.21.6)
echo "[2/6] Downloading Paper server..."
curl -o paper.jar "https://api.papermc.io/v2/projects/paper/versions/1.21.6/builds/47/downloads/paper-1.21.6-47.jar"

# Download MCMMO Classic
echo "[3/6] Downloading MCMMO Classic..."
mkdir -p plugins
curl -L -o plugins/mcMMO.jar "https://jenkins.neetgames.com/job/mcMMO/job/mcMMO-Classic/lastSuccessfulBuild/artifact/target/mcMMO.jar"

# Create start script
echo "[4/6] Creating launch script..."
cat > start-server.sh << 'EOF'
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
EOF

chmod +x start-server.sh

# Accept EULA
echo "[5/6] Accepting EULA..."
echo "eula=true" > eula.txt

# Configure server
echo "[6/6] Configuring server..."
cat > server.properties << EOF
online-mode=false
gamemode=survival
difficulty=normal
max-players=10
server-name=MCMMO Solo Server
spawn-protection=0
allow-flight=true
view-distance=10
simulation-distance=10
motd=Your Personal MCMMO Server - Ready to Level Up!
server-port=25565
enable-command-block=true
op-permission-level=4
pvp=true
EOF

echo ""
echo "✅ Setup complete!"
echo "✅ Run './launch-gui.sh' for GUI or 'cd minecraft-mcmmo-server && ./start-server.sh' for CLI"
echo "✅ Connect to: localhost"
echo ""
echo "📋 IMPORTANT: Ensure Java 21+ is installed (brew install openjdk@21)"
echo "" 