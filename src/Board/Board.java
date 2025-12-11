package Board;

import Game.GameManager;
import Game.GameState;
import Utils.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;

public class Board extends JFrame {

    private Container contentPane;
    private GameManager gameManager;

    private Image bgStartScreen;
    private Image bgModeScreen;

    private ImageIcon iconPlay, iconExit, iconMenu, iconOptions;
    private ImageIcon iconOnePlayer, iconTwoPlayers, iconPlayerVSAI;
    private ImageIcon iconPause, iconStart, mode;
    private ImageIcon iconBack;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        new Board();
    }

    public Board() {
        setTitle("8 Magic Ducks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        gameManager = new GameManager();

        try {
            bgStartScreen = ImageIO.read(new File("src//Assets//Background//3.png"));
            bgModeScreen = ImageIO.read(new File("src//Assets//Background//2.png"));

            iconPlay = resizeIcon("src//Assets//Buttons//play_btn.png", 200, 60);
            iconExit = resizeIcon("src//Assets//Buttons//exit_btn.png", 200, 60);
            iconMenu = resizeIcon("src//Assets//Buttons//menu_btn.png", 200, 60);
            iconOptions = resizeIcon("src//Assets//Buttons//option_btn.png", 200, 60);

            mode = resizeIcon("src//Assets//Buttons//GameMode.png", 250, 100);
            iconOnePlayer = resizeIcon("src//Assets//Buttons//oneP.png", 200, 60);
            iconTwoPlayers = resizeIcon("src//Assets//Buttons//twoP.png", 200, 60);
            iconPlayerVSAI  = resizeIcon("src//Assets//Buttons//VS.png", 200, 60);

            iconPause = new ImageIcon("src//Assets//Buttons//pause_btn.png");
            iconStart = new ImageIcon("src//Assets//Buttons//start_btn.png");

            iconBack = resizeIcon("src//Assets//Buttons//Back_btn.png", 200, 60);

        } catch (Exception e) {
            System.out.println("Error loading images: " + e.getMessage());
        }

        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        Utils.Sound.playMenuMusic();

        showStartScreen();

        setVisible(true);
    }

    private ImageIcon resizeIcon(String path, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image img = originalIcon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private void showStartScreen() {

        gameManager.stopGame(contentPane);

        setSize(420, 650);
        setLocationRelativeTo(null);
        refreshScreen();

        JPanel startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgStartScreen != null) {
                    g.drawImage(bgStartScreen, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));

        startPanel.add(Box.createVerticalStrut(250));

        JButton btnPlay = new JButton(iconPlay);
        makeButtonTransparent(btnPlay);
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                showModeSelectionScreen();
            }
        });

        JButton btnOptions = new JButton(iconOptions);
        makeButtonTransparent(btnOptions);
        btnOptions.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnOptions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                showOptionsDialog();
            }
        });

        JButton btnMenu = new JButton(iconMenu);
        makeButtonTransparent(btnMenu);
        btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                showHighScores();
            }
        });

        JButton btnExit = new JButton(iconExit);
        makeButtonTransparent(btnExit);
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                System.exit(0);
            }
        });

        startPanel.add(btnPlay);
        startPanel.add(Box.createVerticalStrut(20));
        startPanel.add(btnOptions);
        startPanel.add(Box.createVerticalStrut(20));
        startPanel.add(btnMenu);
        startPanel.add(Box.createVerticalStrut(20));
        startPanel.add(btnExit);

        contentPane.add(startPanel, BorderLayout.CENTER);
        refreshScreen();
    }

    private void showOptionsDialog() {
        JDialog optionsDialog = new JDialog(this, "Options", true);
        optionsDialog.setSize(350, 200);
        optionsDialog.setLocationRelativeTo(this);
        optionsDialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sound Volume Label
        JLabel volumeLabel = new JLabel("Sound Volume:");
        volumeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(volumeLabel);

        panel.add(Box.createVerticalStrut(15));

        // Volume Slider
        JSlider volumeSlider = new JSlider(0, 100);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Volume Value Label
        JLabel valueLabel = new JLabel("Volume: 50%");
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Get current volume and set slider
        int currentVolume = (int)(Sound.getVolume() * 100);
        volumeSlider.setValue(currentVolume);
        valueLabel.setText("Volume: " + currentVolume + "%");

        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            valueLabel.setText("Volume: " + value + "%");
            Sound.setVolume(value / 100.0f);
        });

        panel.add(volumeSlider);
        panel.add(Box.createVerticalStrut(10));
        panel.add(valueLabel);

        panel.add(Box.createVerticalStrut(20));

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> {
            Sound.playClick();
            optionsDialog.dispose();
        });

        panel.add(closeButton);

        optionsDialog.add(panel);
        optionsDialog.setVisible(true);
    }

    private void showModeSelectionScreen() {

        contentPane.removeAll();
        refreshScreen();

        JPanel modePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgModeScreen != null) {
                    g.drawImage(bgModeScreen, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        modePanel.setLayout(new BoxLayout(modePanel, BoxLayout.Y_AXIS));

        modePanel.add(Box.createVerticalStrut(100));

        JLabel titleImage = new JLabel(mode);
        titleImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        modePanel.add(titleImage);

        modePanel.add(Box.createVerticalStrut(30));

        JButton btnOnePlayer = new JButton(iconOnePlayer);
        makeButtonTransparent(btnOnePlayer);
        btnOnePlayer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnTwoPlayers = new JButton(iconTwoPlayers);
        makeButtonTransparent(btnTwoPlayers);
        btnTwoPlayers.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnAI = new JButton(iconPlayerVSAI);
        makeButtonTransparent(btnAI);
        btnAI.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add Back Button
        JButton btnBack = new JButton(iconBack);
        makeButtonTransparent(btnBack);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        modePanel.add(btnOnePlayer);
        modePanel.add(Box.createVerticalStrut(10));
        modePanel.add(btnTwoPlayers);
        modePanel.add(Box.createVerticalStrut(10));
        modePanel.add(btnAI);
        modePanel.add(Box.createVerticalStrut(20));
        modePanel.add(btnBack);

        btnOnePlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();

                // Show username input dialog with custom styling
                JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
                JLabel label = new JLabel("Enter your username:");
                label.setFont(new Font("Arial", Font.BOLD, 14));
                JTextField textField = new JTextField(15);
                textField.setFont(new Font("Arial", Font.PLAIN, 14));

                panel.add(label);
                panel.add(textField);

                int result = JOptionPane.showConfirmDialog(
                        Board.this,
                        panel,
                        "Player Name",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                String username = textField.getText().trim();

                // Check if user clicked OK and entered a name
                if (result == JOptionPane.OK_OPTION && !username.isEmpty()) {
                    setSize(615, 638);  // 600x600 game area + window borders
                    setLocationRelativeTo(null);
                    gameManager.startGame(GameState.ONE_PLAYER, contentPane, username);
                } else if (result == JOptionPane.OK_OPTION && username.isEmpty()) {
                    // If OK but empty, show error
                    JOptionPane.showMessageDialog(
                            Board.this,
                            "Please enter a username!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                // If cancelled, just stay on mode selection screen
            }
        });

        btnTwoPlayers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                setSize(1215, 638);  // Two 600x600 boards side by side
                setLocationRelativeTo(null);
                gameManager.startGame(GameState.TWO_PLAYERS, contentPane);
            }
        });

        btnAI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();

                // Show username input dialog with custom styling
                JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
                JLabel label = new JLabel("Enter your username:");
                label.setFont(new Font("Arial", Font.BOLD, 14));
                JTextField textField = new JTextField(15);
                textField.setFont(new Font("Arial", Font.PLAIN, 14));

                panel.add(label);
                panel.add(textField);

                int result = JOptionPane.showConfirmDialog(
                        Board.this,
                        panel,
                        "Player Name",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                String username = textField.getText().trim();

                // Check if user clicked OK and entered a name
                if (result == JOptionPane.OK_OPTION && !username.isEmpty()) {
                    setSize(615, 638);  // 600x600 game area + window borders
                    setLocationRelativeTo(null);
                    gameManager.startGame(GameState.AI_MODE, contentPane, username);
                } else if (result == JOptionPane.OK_OPTION && username.isEmpty()) {
                    // If OK but empty, show error
                    JOptionPane.showMessageDialog(
                            Board.this,
                            "Please enter a username!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                // If cancelled, just stay on mode selection screen
            }
        });

        // Back button action listener
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Sound.playClick();
                contentPane.removeAll();
                showStartScreen();
            }
        });

        contentPane.add(modePanel, BorderLayout.CENTER);
        refreshScreen();
    }

    private void showHighScores() {
        // Create high scores panel
        JPanel scoresPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgModeScreen != null) {
                    g.drawImage(bgModeScreen, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        scoresPanel.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("HIGH SCORES", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.YELLOW);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        scoresPanel.add(title, BorderLayout.NORTH);

        // Scores list
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        java.util.List<Utils.PlayerDataManager.HighScore> topScores =
                Utils.PlayerDataManager.getTopScores(10);

        if (topScores.isEmpty()) {
            JLabel noScores = new JLabel("No scores yet! Play to set a record!");
            noScores.setFont(new Font("Arial", Font.BOLD, 18));
            noScores.setForeground(Color.WHITE);
            noScores.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(noScores);
        } else {
            int rank = 1;
            for (Utils.PlayerDataManager.HighScore score : topScores) {
                JLabel scoreLabel = new JLabel(String.format(
                        "%d. %s - %d points (Level %d)",
                        rank++, score.username, score.score, score.level
                ));
                scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                centerPanel.add(scoreLabel);
                centerPanel.add(Box.createVerticalStrut(10));
            }
        }

        scoresPanel.add(centerPanel, BorderLayout.CENTER);

        // Back button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton backButton = new JButton(iconBack);
        makeButtonTransparent(backButton);
        backButton.addActionListener(e -> {
            Sound.playClick();
            showStartScreen();
        });
        buttonPanel.add(backButton);
        scoresPanel.add(buttonPanel, BorderLayout.SOUTH);

        contentPane.removeAll();
        contentPane.add(scoresPanel, BorderLayout.CENTER);
        refreshScreen();
    }

    private void makeButtonTransparent(JButton btn) {
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void refreshScreen() {
        contentPane.revalidate();
        contentPane.repaint();
    }
}