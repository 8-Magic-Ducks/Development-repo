package Game;

import Board.*;
import Utils.InputHandler;
import com.sun.opengl.util.Animator;

import javax.media.opengl.GLCanvas;
import java.awt.*;

public class GameManager {

    private GLCanvas glCanvas;
    private Animator animator;
    private Player currentPlayer;

    // Original method without username (for backward compatibility)
    public void startGame(GameState mode, Container contentPane) {
        startGame(mode, contentPane, "Player 1");
    }

    // Main method with username parameter
    public void startGame(GameState mode, Container contentPane, String username) {

        contentPane.removeAll();

        glCanvas = new GLCanvas();
        BoardListener listener = null;
        InputHandler input = null;


        if (mode == GameState.ONE_PLAYER) {
            // Create player with the provided username
            currentPlayer = new Player(username, 1);
            OnePlayerGLListener oneP = new OnePlayerGLListener(currentPlayer);
            listener = oneP;
            input = new InputHandler(oneP.getDucks());

        } else if (mode == GameState.TWO_PLAYERS) {

            TwoPlayerGLListener twoP = new TwoPlayerGLListener();
            listener = twoP;
            input = new InputHandler(twoP.getAllDucks());

        } else if (mode == GameState.AI_MODE) {
            // Create player for AI mode too
            currentPlayer = new Player(username, 1);
            AIGLListener ai = new AIGLListener();
            listener = ai;

        }


        if (listener != null) {
            glCanvas.addGLEventListener(listener);
        }


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


    public void stopGame(Container contentPane) {
        if (animator != null && animator.isAnimating()) {
            animator.stop();
        }
        if (glCanvas != null) {
            contentPane.remove(glCanvas);
            glCanvas = null;
        }
        currentPlayer = null;
    }

    // Get current player
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}