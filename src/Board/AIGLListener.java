package Board;

import Entities.Tile;
import Game.AIPlayer;
import Texture.TextureReader;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import java.io.IOException;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class AIGLListener extends BoardListener {

    private AIPlayer aiPlayer;
    private List<Tile> aiSolution;
    String[] textureNames;
    TextureReader.Texture[] textures;
    int[] textureIds;

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

        textureNames = new String[] { background + ".png", D1 + ".png" };
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

        aiPlayer = new AIPlayer();
        if (aiPlayer.solve()) {
            aiSolution = aiPlayer.getSolution();
        }
    }

    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureIds[0]);
        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(400, 0);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(400, 400);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(0, 400);
        gl.glEnd();

        if (aiSolution != null) {
            for (Tile duck : aiSolution) {
                Tile drawDuck = new Tile(duck.getX(), duck.getY(), textureIds[1]);
                drawDuck.draw(gl);
            }
        }
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