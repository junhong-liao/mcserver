import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class MinecraftServerGUI extends JFrame {
    private static final Color BG_COLOR = new Color(45, 45, 45);
    private static final Color BUTTON_COLOR = new Color(70, 70, 70);
    private static final Color BUTTON_HOVER = new Color(90, 90, 90);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private Process serverProcess;
    private boolean serverRunning = false;

    public MinecraftServerGUI() {
        setTitle("Minecraft Server Control");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Hide instead of exit to persist
        setResizable(false);
        createSimpleGUI();
        centerWindow();
        setAlwaysOnTop(false); // Allow minimizing
    }

    private void createSimpleGUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title label
        JLabel titleLabel = new JLabel("Minecraft Server Control");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Start Server Button
        JButton startServerBtn = createStyledButton("Start Current Server");
        startServerBtn.addActionListener(e -> startCurrentServer());
        mainPanel.add(startServerBtn);
        mainPanel.add(Box.createVerticalStrut(10));

        // New World with Seed Button
        JButton newWorldBtn = createStyledButton("New World with Seed");
        newWorldBtn.addActionListener(e -> createNewWorldWithSeed());
        mainPanel.add(newWorldBtn);
        mainPanel.add(Box.createVerticalStrut(15));

        // Status label
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        add(mainPanel);
        pack();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }

    private void startCurrentServer() {
        if (serverRunning) {
            JOptionPane.showMessageDialog(this, "Server is already running!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("bash", "start-server.sh");
                pb.directory(new File("."));
                pb.redirectErrorStream(true);

                serverProcess = pb.start();
                serverRunning = true;
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Server started successfully!\nCheck the terminal for server output.", 
                        "Server Started", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
                // Wait for process to complete
                serverProcess.waitFor();
                serverRunning = false;
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to start server: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                });
                serverRunning = false;
            }
        }).start();
    }

    private void createNewWorldWithSeed() {
        if (serverRunning) {
            JOptionPane.showMessageDialog(this, "Please stop the server before creating a new world!", "Server Running", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seed = JOptionPane.showInputDialog(this, 
            "Enter world seed (leave empty for random):", 
            "New World Seed", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (seed == null) return; // User cancelled
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "This will backup your current world and create a new one.\nContinue?",
            "Confirm New World",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        new Thread(() -> {
            try {
                // Backup current world
                backupCurrentWorld();
                
                // Update server.properties with new seed
                updateServerProperties(seed);
                
                // Delete current world directories
                deleteWorldDirectories();
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "New world configuration saved!\nStart the server to generate the new world.",
                        "New World Ready",
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to create new world: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void backupCurrentWorld() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String backupDirName = "world_backup_" + timestamp;
        
        // Create backup directory
        Path backupDir = Paths.get(backupDirName);
        Files.createDirectories(backupDir);
        
        // Backup world directories
        copyDirectory("world", backupDir.resolve("world"));
        copyDirectory("world_nether", backupDir.resolve("world_nether"));
        copyDirectory("world_the_end", backupDir.resolve("world_the_end"));
        
        System.out.println("World backed up to: " + backupDirName);
    }

    private void copyDirectory(String source, Path target) throws IOException {
        Path sourcePath = Paths.get(source);
        if (!Files.exists(sourcePath)) return;
        
        Files.walk(sourcePath).forEach(src -> {
            try {
                Path dest = target.resolve(sourcePath.relativize(src));
                if (Files.isDirectory(src)) {
                    Files.createDirectories(dest);
                } else {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateServerProperties(String seed) throws IOException {
        Properties props = new Properties();
        File propsFile = new File("server.properties");
        
        if (propsFile.exists()) {
            try (FileInputStream in = new FileInputStream(propsFile)) {
                props.load(in);
            }
        }
        
        // Update seed
        props.setProperty("level-seed", seed != null ? seed.trim() : "");
        
        // Save properties
        try (FileOutputStream out = new FileOutputStream(propsFile)) {
            props.store(out, "Minecraft server properties");
        }
    }

    private void deleteWorldDirectories() throws IOException {
        deleteDirectoryIfExists("world");
        deleteDirectoryIfExists("world_nether");
        deleteDirectoryIfExists("world_the_end");
    }

    private void deleteDirectoryIfExists(String dirName) throws IOException {
        Path dir = Paths.get(dirName);
        if (Files.exists(dir)) {
            Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Failed to delete: " + path);
                    }
                });
        }
    }

    private void centerWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getPreferredSize();
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        setLocation(x, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use default look and feel
            }
            
            new MinecraftServerGUI().setVisible(true);
        });
    }
} 