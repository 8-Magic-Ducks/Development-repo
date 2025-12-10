package Board;

import Entities.Tile;
import Texture.TextureReader;
import Utils.Collision;
import Utils.Timer;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TwoPlayerGLListener extends BoardListener {

    private List<Tile> allDucks = new ArrayList<Tile>();
    private List<Tile> player1Ducks = new ArrayList<Tile>();
    private List<Tile> player2Ducks = new ArrayList<Tile>();
    private Timer timer;
    private String winner = "";
    String[] textureNames;
    TextureReader.Texture[] textures;
    int[] textureIds;

    public TwoPlayerGLListener() {
        timer = new Timer(120);
    }

    public List<Tile> getAllDucks() {
        return allDucks;
    }

    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 800, 0, 600, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_BLEND);

        textureNames = new String[] { background + ".png", D1 + ".png", D2 + ".png", D3 + ".png", D4 + ".png", D5 + ".png", D6 + ".png", D7 + ".png", D8 + ".png" };
        textures = new TextureReader.Texture[textureNames.length];
        textureIds = new int[textureNames.length];
        gl.glGenTextures(textureNames.length, textureIds, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                textures[i] = TextureReader.readTexture(textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[i]);
                new GLU().gluBuild2DMipmaps(GL.GL_TEXTURE_2D, GL.GL_RGBA, textures[i].getWidth(), textures[i].getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, textures[i].getPixels());
            } catch (IOException e) { e.printStackTrace(); }
        }

        generateLevels();
        timer.start();
    }

    private void generateLevels() {
        Random rand = new Random();
        for(int i=1; i<=8; i++) {
            Tile t = new Tile(rand.nextInt(8), rand.nextInt(8), textureIds[i]);
            player1Ducks.add(t);
            allDucks.add(t);
        }
        for(int i=1; i<=8; i++) {
            Tile t = new Tile(rand.nextInt(8) + 8, rand.nextInt(8), textureIds[i]);
            player2Ducks.add(t);
            allDucks.add(t);
        }
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        drawBackground(gl, 0);
        drawBackground(gl, 400);

        if (winner.isEmpty()) {
            for (Tile duck : player1Ducks) {
                boolean attacked = Collision.isAttacked(duck, player1Ducks);
                duck.setSafe(!attacked);
                duck.draw(gl);
            }
            if(Collision.isBoardSolved(player1Ducks)) winner = "Player 1 Wins!";

            for (Tile duck : player2Ducks) {
                boolean attacked = Collision.isAttacked(duck, player2Ducks);
                duck.setSafe(!attacked);
                duck.draw(gl);
            }
            if(Collision.isBoardSolved(player2Ducks)) winner = "Player 2 Wins!";

        } else {
            System.out.println(winner);
            gl.glClearColor(0, 1, 0, 1);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        }
    }

    private void drawBackground(GL gl, int xOffset) {
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[0]);
        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(xOffset, 0);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(xOffset + 400, 0);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(xOffset + 400, 400);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(xOffset, 400);
        gl.glEnd();
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