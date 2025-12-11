package Game;

import Board.*;
import Utils.InputHandler;
import com.sun.opengl.util.Animator;

import javax.media.opengl.GLCanvas;
import java.awt.*;

public class GameManager {

    private GLCanvas glCanvas;
    private Animator animator;

    public void startGame(GameState mode, Container contentPane) {

        contentPane.removeAll();

        glCanvas = new GLCanvas();
        BoardListener listener = null;
        InputHandler input = null;


        if (mode == GameState.ONE_PLAYER) {
            OnePlayerGLListener oneP = new OnePlayerGLListener();
            listener = oneP;
            input = new InputHandler(oneP.getDucks());

        } else if (mode == GameState.TWO_PLAYERS) {
            TwoPlayerGLListener twoP = new TwoPlayerGLListener();
            listener = twoP;
            input = new InputHandler(twoP.getAllDucks());

        } else if (mode == GameState.AI_MODE) {
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
    }
}