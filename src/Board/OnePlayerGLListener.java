package Board;

import Entities.Tile;
import Game.Player;
import Texture.TextureReader;
import Utils.Collision;
import Utils.Timer;
import Utils.Sound;
import Utils.PlayerDataManager;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class OnePlayerGLListener extends BoardListener {

    private List<Tile> ducks = new ArrayList<Tile>();
    private Timer timer;
    private boolean isGameOver = false;
    private int score = 0;
    private int boardsSolved = 0;
    private boolean hasWon = false;
    private int lives = 3;

    private Player player;

    String[] textureNames;
    TextureReader.Texture[] textures;
    int[] textureIds;

    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 50;
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int BOARD_WIDTH = BOARD_SIZE * TILE_SIZE;
    private static final int BOARD_OFFSET_X = 0;
    private static final int BOARD_OFFSET_Y = 0;

    // Indices for number textures in textureIds array
    private static final int NUMBER_TEXTURE_START = 4; // Starting index for number textures

    public OnePlayerGLListener(Player player) {
        this.player = player;
        this.lives = player.getLives();
        this.score = player.getScore();
        timer = new Timer(60);
    }

    public List<Tile> getDucks() {
        return ducks;
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_BLEND);

        textureNames = new String[] {
                background + "2.png",
                background + "p1.png",  // Light tile
                background + "p2.png",  // Dark tile
                background + "life.png", // Heart/life icon
                // Number textures (0-9)
                Nums + "0.png", Nums + "1.png", Nums + "2.png", Nums + "3.png", Nums + "4.png",
                Nums + "5.png", Nums + "6.png", Nums + "7.png", Nums + "8.png", Nums + "9.png",
                // Duck textures
                D1 + "f1.png", D2 + "f1.png", D3 + "f1.png", D4 + "f1.png",
                D5 + "f1.png", D6 + "f1.png", D7 + "f1.png", D8 + "f1.png"
        };

        textures = new TextureReader.Texture[textureNames.length];
        textureIds = new int[textureNames.length];

        gl.glGenTextures(textureNames.length, textureIds, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                textures[i] = TextureReader.readTexture(textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[i]);
                new GLU().gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, textures[i].getWidth(),
                        textures[i].getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, textures[i].getPixels());
            } catch (IOException e) {
                System.err.println("Error loading texture: " + textureNames[i]);
                e.printStackTrace();
            }
        }

        generateLevel();
        timer.start();
    }

    private void generateLevel() {
        ducks.clear();
        Random rand = new Random();
        for (int i = 14; i <= 21; i++) { // Updated indices for duck textures
            ducks.add(new Tile(rand.nextInt(8), rand.nextInt(8), textureIds[i]));
        }
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (!isGameOver) {
            drawBackground(gl);
            drawChessBoard(gl);

            for (Tile duck : ducks) {
                boolean attacked = Collision.isAttacked(duck, ducks);
                duck.setSafe(!attacked);
                duck.draw(gl);
            }

            drawTimerBar(gl);
            drawLives(gl);
            drawScore(gl);

            // Check if player won FIRST (before checking time)
            boolean allSafe = true;
            for (Tile duck : ducks) {
                if (!duck.isSafe()) {
                    allSafe = false;
                    break;
                }
            }

            if (allSafe && ducks.size() == 8) {
                // Player solved the board!
                int remainingSeconds = (int)(timer.getRemaining() / 1000);
                score += remainingSeconds;
                boardsSolved++;

                timer.reset();
                timer.start();
                generateLevel();
            }
            // Check if time is up AFTER checking for win
            else if (timer.isTimeUp()) {
                Sound.playBadMove();
                lives--;

                if (lives <= 0) {
                    isGameOver = true;
                    System.out.println("Game Over! Final Score: " + score);
                } else {
                    timer.reset();
                    timer.start();
                    generateLevel();
                }
            }
        } else {
            gl.glClearColor(1, 0, 0, 1);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        }
    }

    private void drawBackground(GL gl) {
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[0]);
        gl.glColor3f(1, 1, 1);

        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        float width = viewport[2];
        float height = viewport[3];

        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(width, 0);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(width, height);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(0, height);
        gl.glEnd();

        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    private void drawChessBoard(GL gl) {
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean isLightTile = (row + col) % 2 == 0;
                int textureIndex = isLightTile ? 1 : 2;

                gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[textureIndex]);

                float x = BOARD_OFFSET_X + col * TILE_SIZE;
                float y = BOARD_OFFSET_Y + row * TILE_SIZE;

                gl.glBegin(GL.GL_QUADS);
                gl.glTexCoord2f(0, 0); gl.glVertex2f(x, y);
                gl.glTexCoord2f(1, 0); gl.glVertex2f(x + TILE_SIZE, y);
                gl.glTexCoord2f(1, 1); gl.glVertex2f(x + TILE_SIZE, y + TILE_SIZE);
                gl.glTexCoord2f(0, 1); gl.glVertex2f(x, y + TILE_SIZE);
                gl.glEnd();
            }
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    private void drawTimerBar(GL gl) {
        float remainingPercent = (float) timer.getRemaining() / 60000.0f;

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(10, SCREEN_HEIGHT - 30);
        gl.glVertex2f(SCREEN_WIDTH - 10, SCREEN_HEIGHT - 30);
        gl.glVertex2f(SCREEN_WIDTH - 10, SCREEN_HEIGHT - 10);
        gl.glVertex2f(10, SCREEN_HEIGHT - 10);
        gl.glEnd();

        if (remainingPercent > 0.5) gl.glColor3f(0.0f, 1.0f, 0.0f);
        else if (remainingPercent > 0.2) gl.glColor3f(1.0f, 1.0f, 0.0f);
        else gl.glColor3f(1.0f, 0.0f, 0.0f);

        float barWidth = (SCREEN_WIDTH - 20) * remainingPercent;
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(10, SCREEN_HEIGHT - 28);
        gl.glVertex2f(10 + barWidth, SCREEN_HEIGHT - 28);
        gl.glVertex2f(10 + barWidth, SCREEN_HEIGHT - 12);
        gl.glVertex2f(10, SCREEN_HEIGHT - 12);
        gl.glEnd();
    }

    private void drawLives(GL gl) {
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[3]);
        gl.glColor3f(1, 1, 1);

        float heartSize = 30;
        float startX = 10;
        float startY = SCREEN_HEIGHT - 70;

        for (int i = 0; i < lives; i++) {
            float x = startX + (i * (heartSize + 5));

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(x, startY);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(x + heartSize, startY);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(x + heartSize, startY + heartSize);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(x, startY + heartSize);
            gl.glEnd();
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    /**
     * Draw the score using number textures at the specified position
     */
    private void drawScore(GL gl) {
        float digitWidth = 25;   // Width of each digit
        float digitHeight = 35;  // Height of each digit
        float digitSpacing = 5;  // Space between digits

        // Position for score display (top right area)
        float startX = SCREEN_WIDTH - 120;
        float startY = SCREEN_HEIGHT - 65;

        // Convert score to string to get individual digits
        String scoreStr = String.valueOf(score);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);

        // Draw each digit
        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));

            // Get texture index for this digit (4-13 are the number textures 0-9)
            int textureIndex = NUMBER_TEXTURE_START + digit;

            gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[textureIndex]);

            float x = startX + (i * (digitWidth + digitSpacing));

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(x, startY);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(x + digitWidth, startY);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(x + digitWidth, startY + digitHeight);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(x, startY + digitHeight);
            gl.glEnd();
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    /**
     * Helper method to draw a number at any position
     * Can be used for other numeric displays like timer, level, etc.
     */
    private void drawNumber(GL gl, int number, float x, float y, float digitWidth, float digitHeight) {
        String numStr = String.valueOf(number);
        float digitSpacing = 5;

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 1);

        for (int i = 0; i < numStr.length(); i++) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            int textureIndex = NUMBER_TEXTURE_START + digit;

            gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[textureIndex]);

            float digitX = x + (i * (digitWidth + digitSpacing));

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(digitX, y);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(digitX + digitWidth, y);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(digitX + digitWidth, y + digitHeight);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(digitX, y + digitHeight);
            gl.glEnd();
        }

        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {}
    public void displayChanged(GLAutoDrawable d, boolean m, boolean dev) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}