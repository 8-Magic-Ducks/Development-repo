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

public class OnePlayerGLListener extends BoardListener {

    private List<Tile> ducks = new ArrayList<Tile>();
    private Timer timer;
    private boolean isGameOver = false;

    String[] textureNames;
    TextureReader.Texture[] textures;
    int[] textureIds;

    public OnePlayerGLListener() {
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
        gl.glOrtho(0, 400, 0, 600, -1, 1);
        gl.glMatrixMode(GL.GL_MODELVIEW);

        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL.GL_BLEND);

        textureNames = new String[] {
                background + ".png",
                D1 + ".png", D2 + ".png", D3 + ".png", D4 + ".png",
                D5 + ".png", D6 + ".png", D7 + ".png", D8 + ".png"
        };

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

        generateLevel();
        timer.start();
    }

    private void generateLevel() {
        ducks.clear();
        Random rand = new Random();
        for (int i = 1; i <= 8; i++) {
            ducks.add(new Tile(rand.nextInt(8), rand.nextInt(8), textureIds[i]));
        }
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (!isGameOver) {
            drawBackground(gl);

            for (Tile duck : ducks) {
                boolean attacked = Collision.isAttacked(duck, ducks);
                duck.setSafe(!attacked);
                duck.draw(gl);
            }
            drawTimerBar(gl);

            if (timer.isTimeUp()) {
                isGameOver = true;
                System.out.println("Time's Up!");
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
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(400, 0);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(400, 400);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(0, 400);
        gl.glEnd();
        gl.glDisable(GL.GL_TEXTURE_2D);
    }

    private void drawTimerBar(GL gl) {
        float remainingPercent = (float) timer.getRemaining() / 60000.0f;
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(0, 400);
        gl.glVertex2f(400, 400);
        gl.glVertex2f(400, 600);
        gl.glVertex2f(0, 600);
        gl.glEnd();

        if (remainingPercent > 0.5) gl.glColor3f(0.0f, 1.0f, 0.0f);
        else if (remainingPercent > 0.2) gl.glColor3f(1.0f, 1.0f, 0.0f);
        else gl.glColor3f(1.0f, 0.0f, 0.0f);

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(10, 550);
        gl.glVertex2f(10 + (380 * remainingPercent), 550);
        gl.glVertex2f(10 + (380 * remainingPercent), 580);
        gl.glVertex2f(10, 580);
        gl.glEnd();
        gl.glColor3f(1, 1, 1);
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