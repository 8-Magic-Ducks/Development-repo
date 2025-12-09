package Board;

import Utils.InputHandler;
import com.sun.opengl.util.Animator;

import javax.media.opengl.GLCanvas;
import javax.swing.*;
import java.awt.*;

public class Board extends JFrame {
    private int player;

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        new Board();
    }

    public Board() {
        setTitle("8 Magic Ducks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        GLCanvas glCanvas = new GLCanvas();

        OnePlayerGLListener listener = new OnePlayerGLListener();
        glCanvas.addGLEventListener(listener);

        InputHandler input = new InputHandler(listener.getDucks());
        glCanvas.addMouseListener(input);
        glCanvas.addMouseMotionListener(input);

        Animator animator = new Animator(glCanvas);
        animator.start();

        add(glCanvas, BorderLayout.CENTER);
        setVisible(true);
        glCanvas.requestFocus();

    }
}