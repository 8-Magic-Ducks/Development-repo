package Board;

import Utils.InputHandler;
import com.sun.opengl.util.Animator;
import javax.media.opengl.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends JFrame {

    private Container contentPane;
    private GLCanvas glCanvas;
    private Animator animator;

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

        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        showMainMenu();
        setVisible(true);
    }

    private void showMainMenu() {
        if (glCanvas != null) {
            contentPane.remove(glCanvas);
            if (animator != null && animator.isAnimating()) {
                animator.stop();
            }
        }

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.DARK_GRAY);

        menuPanel.add(Box.createVerticalStrut(150));

        JLabel title = new JLabel("Magic Ducks Puzzle");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(title);

        menuPanel.add(Box.createVerticalStrut(50));

        JButton btnOnePlayer = createStyledButton("Single Player");
        JButton btnTwoPlayers = createStyledButton("Two Players");
        JButton btnAI = createStyledButton("Play vs AI");

        menuPanel.add(btnOnePlayer);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnTwoPlayers);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(btnAI);

        btnOnePlayer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame("ONE_PLAYER");
            }
        });

        btnTwoPlayers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame("TWO_PLAYERS");
            }
        });

        btnAI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame("AI_MODE");
            }
        });

        contentPane.add(menuPanel, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setMaximumSize(new Dimension(300, 50));
        return btn;
    }

    private void startGame(String mode) {
        contentPane.removeAll();
        glCanvas = new GLCanvas();
        BoardListener listener = null;
        InputHandler input = null;
        if ("ONE_PLAYER".equals(mode)) {
            setSize(420, 650);
            setLocationRelativeTo(null);
            OnePlayerGLListener oneP = new OnePlayerGLListener();
            listener = oneP;
            input = new InputHandler(oneP.getDucks());

        } else if ("TWO_PLAYERS".equals(mode)) {
            setSize(840, 650);
            setLocationRelativeTo(null);
            TwoPlayerGLListener twoP = new TwoPlayerGLListener();
            listener = twoP;
            input = new InputHandler(twoP.getAllDucks());

        } else if ("AI_MODE".equals(mode)) {
            setSize(420, 650);
            setLocationRelativeTo(null);
            AIGLListener ai = new AIGLListener();
            listener = ai;
        }

        glCanvas.addGLEventListener(listener);
        if (input != null) {
            glCanvas.addMouseListener(input);
            glCanvas.addMouseMotionListener(input);
        }

        contentPane.add(glCanvas, BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
        glCanvas.requestFocus();

        animator = new Animator(glCanvas);
        animator.start();
    }
}