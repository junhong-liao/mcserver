# Minecraft MCMMO Server

A fully automated, single-player optimized Minecraft server setup featuring **MCMMO Classic** for skill progression and leveling.

## 📋 Overview

This is a **Paper-based Minecraft 1.21.6 server** configured for:
- **Single-player MCMMO gameplay** with skill leveling system
- **Automated setup and deployment** via shell scripts
- **Production-ready configuration** with optimized JVM flags
- **Plugin ecosystem** including Spark performance monitoring

### Current State ✅

The server is **fully configured and operational** with:
- ✅ Paper 1.21.6 server installed
- ✅ MCMMO Classic plugin active
- ✅ EULA accepted
- ✅ Optimized server properties for single-player experience
- ✅ Performance monitoring with Spark plugin
- ✅ JVM memory allocation: 4GB max, 2GB min with G1GC
- ✅ Auto-restart functionality

---

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (Required for Paper 1.21.6!) - Install with: `brew install openjdk@21`
- **4GB+ RAM** available for server allocation
- **Minecraft Java Edition** client

### 🎮 GUI Launcher (Recommended)

1. **Launch the GUI:**
   ```bash
   ./launch-gui.sh
   ```

2. **Click the grass block** to start your server!

3. **Connect from Minecraft:**
   - Open Minecraft → Multiplayer → Add Server
   - **Server Address:** `localhost`
   - Connect and start playing!

### Command Line Alternative

1. **Navigate to server directory:**
   ```bash
   cd minecraft-mcmmo-server
   ```

2. **Launch server:**
   ```bash
   ./start-server.sh
   ```

3. **Wait for "Done!" message** (typically 15-30 seconds)

4. **Connect from Minecraft:**
   - Server Address: `localhost`

---

## 🎮 MCMMO Commands

| Command | Description | Example |
|---------|-------------|---------|
| `/mcstats` | View your skill levels | Shows all skill XP/levels |
| `/mctop [skill]` | Leaderboards | `/mctop mining` |
| `/mcability` | Toggle skill abilities | Enable/disable special abilities |
| `/mcscoreboard` | Toggle skill scoreboard | Show skills on screen |
| `/inspect [player]` | View player stats | `/inspect Steve` |

## 🔧 Server Management

- **Stop Server:** `Ctrl+C` in terminal (auto-restarts by default)
- **Admin Commands:** Use `/op <username>` to grant admin privileges
- **Performance Monitoring:** `/spark profiler` commands
- **Configuration:** Edit `server.properties` for server settings

## 📁 File Structure

```
minecraft-mcmmo-server/
├── paper.jar                    # Paper server executable
├── MinecraftServerGUI.java      # GUI launcher source code
├── launch-gui.sh               # GUI launcher script
├── start-server.sh             # Command line launch script
├── server.properties           # Server configuration
├── eula.txt                    # License acceptance
├── plugins/
│   ├── mcMMO.jar              # MCMMO Classic plugin
│   └── spark/                 # Performance monitoring
├── world/                     # Generated world data
└── cache/                     # Downloaded dependencies
```

---

## 🛠️ Initial Setup

If setting up from scratch, run:

```bash
./setup.sh
```

This will:
1. Create server directory
2. Download Paper 1.21.6
3. Download MCMMO Classic
4. Configure optimized settings
5. Accept EULA
6. Create launch scripts

---

## 🔧 Configuration

### Memory Settings
Default: 4GB max, 2GB min. Modify in `start-server.sh`:
```bash
java -Xmx4G -Xms2G ...
```

### Server Properties
Key settings in `server.properties`:
- `online-mode=false` - Allows offline/cracked clients
- `gamemode=survival` - Default game mode
- `max-players=10` - Player limit
- `view-distance=10` - Render distance

### MCMMO Configuration
Plugin configs located in `plugins/mcMMO/`:
- `config.yml` - Main MCMMO settings
- `advanced.yml` - Advanced features
- `child.yml` - Child skill configurations

---

## 🎯 Tips

- Use `/op <yourname>` to give yourself admin permissions
- Check `/mcstats` to see your skill progression
- The server auto-restarts if it crashes
- Backups are stored in `plugins/mcMMO/backup/`
- Use Spark plugin for performance monitoring: `/spark profiler start`

## 🆘 Troubleshooting

**Server won't start:**
- Ensure Java 21+ is installed: `brew install openjdk@21`
- Check available RAM (needs 4GB+)
- Verify paper.jar downloaded correctly

**Java version error:**
- Paper 1.21.6 requires Java 21 or higher
- Install Java 21: `brew install openjdk@21`
- The start script automatically uses Java 21 if available

**Can't connect:**
- Ensure server shows "Done!" message
- Try `localhost:25565` as server address
- Check firewall settings

**MCMMO not working:**
- Verify mcMMO.jar is in plugins/ folder
- Check server logs for plugin errors
- Ensure you're not in creative mode 