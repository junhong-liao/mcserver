import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class MinecraftServerGUI extends JFrame {
    private static final Color INVENTORY_BG = new Color(139, 139, 139);
    private static final Color SLOT_COLOR = new Color(85, 85, 85);
    private static final Color GRASS_GREEN = new Color(91, 165, 38);
    private static final Color DIRT_BROWN = new Color(134, 96, 67);
    
    private Process serverProcess;
    private boolean serverRunning = false;

    public MinecraftServerGUI() {
        setTitle("Minecraft Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        createInventoryGUI();
        centerWindow();
        setAlwaysOnTop(true);
    }

    private void createInventoryGUI() {
        // Main panel with inventory background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 3, 2, 2));
        mainPanel.setBackground(INVENTORY_BG);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Create 9 inventory slots (3x3 grid)
        for (int i = 0; i < 9; i++) {
            if (i == 4) { // Center slot (grass block)
                JButton grassBlock = createGrassBlockButton();
                mainPanel.add(grassBlock);
            } else { // Empty slots
                JPanel emptySlot = createEmptySlot();
                mainPanel.add(emptySlot);
            }
        }

        add(mainPanel);
        pack();
    }

    private JPanel createEmptySlot() {
        JPanel slot = new JPanel();
        slot.setPreferredSize(new Dimension(50, 50));
        slot.setBackground(SLOT_COLOR);
        slot.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createLineBorder(new Color(55, 55, 55), 1)
        ));
        return slot;
    }

    private JButton createGrassBlockButton() {
        JButton grassBlock = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                
                int width = getWidth();
                int height = getHeight();
                
                // Draw grass block texture
                // Grass top (green)
                g2d.setColor(GRASS_GREEN);
                g2d.fillRect(0, 0, width, height/3);
                
                // Dirt bottom (brown)
                g2d.setColor(DIRT_BROWN);
                g2d.fillRect(0, height/3, width, 2*height/3);
                
                // Add grass texture details
                g2d.setColor(GRASS_GREEN.darker());
                for (int i = 2; i < width; i += 8) {
                    g2d.drawLine(i, height/3-1, i, height/3+1);
                }
                
                // Add pixelated texture
                g2d.setColor(GRASS_GREEN.brighter());
                for (int x = 0; x < width; x += 4) {
                    for (int y = 0; y < height/3; y += 4) {
                        if ((x + y) % 8 == 0) {
                            g2d.fillRect(x, y, 2, 2);
                        }
                    }
                }
                
                g2d.setColor(DIRT_BROWN.brighter());
                for (int x = 0; x < width; x += 4) {
                    for (int y = height/3; y < height; y += 4) {
                        if ((x + y) % 8 == 0) {
                            g2d.fillRect(x, y, 2, 2);
                        }
                    }
                }
                
                g2d.dispose();
            }
        };
        
        grassBlock.setPreferredSize(new Dimension(50, 50));
        grassBlock.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createLineBorder(new Color(55, 55, 55), 1)
        ));
        grassBlock.setFocusPainted(false);
        grassBlock.setContentAreaFilled(false);
        
        grassBlock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!serverRunning) {
                    startServer();
                }
            }
        });
        
        return grassBlock;
    }

    private void startServer() {
        if (serverRunning) return;
        
        new Thread(() -> {
            try {
                // Use the same Java path as the start-server.sh script
                String javaPath = "/opt/homebrew/opt/openjdk@21/bin/java";
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
                serverRunning = true;
                
                // Hide the GUI after starting server
                SwingUtilities.invokeLater(() -> {
                    setVisible(false);
                    dispose();
                });
                
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Failed to start server: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }).start();
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