import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class MCMMOServerLauncher extends JFrame {
    private static final Color MINECRAFT_GRAY = new Color(139, 139, 139);
    private static final Color DARK_GRAY = new Color(85, 85, 85);
    private static final Color LIGHT_GRAY = new Color(198, 198, 198);
    private static final Color GRASS_GREEN = new Color(91, 165, 38);
    private static final Color DIRT_BROWN = new Color(134, 96, 67);
    
    private JLabel statusLabel;
    private JButton startButton;
    private Process serverProcess;
    private boolean serverRunning = false;

    public MCMMOServerLauncher() {
        setTitle("MCMMO Server Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initializeUI();
        centerWindow();
    }

    private void initializeUI() {
        // Main panel with Minecraft-style background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(MINECRAFT_GRAY);
        mainPanel.setBorder(createMinecraftBorder());

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(MINECRAFT_GRAY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("MCMMO Server Launcher", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Center panel with inventory-style grid
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(MINECRAFT_GRAY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Create grass block button
        startButton = createGrassBlockButton();
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(startButton, gbc);

        // Status label
        statusLabel = new JLabel("Ready to start server", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        statusLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(statusLabel, gbc);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(MINECRAFT_GRAY);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JLabel infoLabel = new JLabel("<html><center>Server will start at <b>localhost:25565</b><br/>Connect from Minecraft to play!</center></html>");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(infoLabel);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
    }

    private JButton createGrassBlockButton() {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw grass block
                if (!serverRunning) {
                    // Grass top
                    g2d.setColor(GRASS_GREEN);
                    g2d.fillRect(0, 0, width, height/3);
                    
                    // Dirt middle and bottom
                    g2d.setColor(DIRT_BROWN);
                    g2d.fillRect(0, height/3, width, 2*height/3);
                    
                    // Add texture lines
                    g2d.setColor(GRASS_GREEN.darker());
                    for (int i = 0; i < width; i += 4) {
                        g2d.drawLine(i, height/3-2, i, height/3+2);
                    }
                } else {
                    // Server running - show diamond block
                    g2d.setColor(new Color(93, 219, 213));
                    g2d.fillRect(0, 0, width, height);
                    
                    // Diamond pattern
                    g2d.setColor(new Color(177, 255, 252));
                    g2d.fillRect(width/4, height/4, width/2, height/2);
                }
                
                // Button border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, width-1, height-1);
                
                // Button text
                String text = serverRunning ? "STOP" : "START";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (width - fm.stringWidth(text)) / 2;
                int textY = (height + fm.getAscent()) / 2 - 2;
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
        };
        
        button.setPreferredSize(new Dimension(100, 100));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!serverRunning) {
                    startServer();
                } else {
                    stopServer();
                }
            }
        });
        
        return button;
    }

    private Border createMinecraftBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_GRAY, 2),
                BorderFactory.createLoweredBevelBorder()
            )
        );
    }

    private void startServer() {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Starting server...");
                    statusLabel.setForeground(Color.YELLOW);
                });

                // Use Java 21 explicitly
                String javaPath = "/opt/homebrew/bin/java";
                File javaFile = new File(javaPath);
                if (!javaFile.exists()) {
                    javaPath = "java"; // fallback to system java
                }

                ProcessBuilder pb = new ProcessBuilder(
                    javaPath,
                    "-Xmx4G",
                    "-Xms2G", 
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                    "-jar", "paper.jar",
                    "--nogui"
                );
                pb.directory(new File("."));
                pb.redirectErrorStream(true);

                serverProcess = pb.start();
                
                // Monitor server output
                BufferedReader reader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String finalLine = line;
                    SwingUtilities.invokeLater(() -> {
                        if (finalLine.contains("Done (") && finalLine.contains("s)!")) {
                            statusLabel.setText("Server running at localhost:25565");
                            statusLabel.setForeground(Color.GREEN);
                            serverRunning = true;
                            startButton.repaint();
                        } else if (finalLine.contains("Stopping server")) {
                            statusLabel.setText("Server stopped");
                            statusLabel.setForeground(Color.RED);
                            serverRunning = false;
                            startButton.repaint();
                        }
                    });
                }
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                });
                ex.printStackTrace();
            }
        }).start();
    }

    private void stopServer() {
        if (serverProcess != null && serverProcess.isAlive()) {
            try {
                // Send stop command
                OutputStreamWriter writer = new OutputStreamWriter(serverProcess.getOutputStream());
                writer.write("stop\n");
                writer.flush();
                writer.close();
                
                statusLabel.setText("Stopping server...");
                statusLabel.setForeground(Color.YELLOW);
                
                // Wait for graceful shutdown
                new Thread(() -> {
                    try {
                        serverProcess.waitFor();
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Ready to start server");
                            statusLabel.setForeground(Color.WHITE);
                            serverRunning = false;
                            startButton.repaint();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
                
            } catch (Exception ex) {
                statusLabel.setText("Error stopping server");
                statusLabel.setForeground(Color.RED);
                ex.printStackTrace();
            }
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
        // Set Minecraft-style look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MCMMOServerLauncher().setVisible(true);
        });
    }
} 