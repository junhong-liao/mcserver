#!/bin/bash
echo "================================"
echo " MCMMO Single-Player Auto-Setup"
echo "================================"

# Create server directory
echo "[1/6] Creating server directory..."
mkdir -p minecraft-mcmmo-server
cd minecraft-mcmmo-server

# Download Paper server
echo "[2/6] Downloading Paper server..."
curl -L -o paper.jar "https://api.papermc.io/v2/projects/paper/versions/1.21.6/builds/latest/downloads/paper-1.21.6-latest.jar"

# Download MCMMO Classic
echo "[3/6] Downloading MCMMO Classic..."
mkdir -p plugins
curl -L -o plugins/mcMMO.jar "https://jenkins.neetgames.com/job/mcMMO/job/mcMMO-Classic/lastSuccessfulBuild/artifact/target/mcMMO.jar"

# Create start script
echo "[4/6] Creating launch script..."
cat > start-server.sh << 'EOF'
#!/bin/bash
while true; do
    echo "Starting MCMMO server..."
    java -Xmx4G -Xms2G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -jar paper.jar --nogui
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
motd=Solo Leveling
EOF

echo ""
echo "✅ Setup complete!"
echo "✅ Run './start-server.sh' to launch"
echo "✅ Connect to: localhost"
echo "" 