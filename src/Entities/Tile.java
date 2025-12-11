package Entities;

import javax.media.opengl.GL;

public class Tile {
    private int x;
    private int y;
    private int texture;
    private boolean isHighlighted;
    private boolean isSafe;

    public Tile(int x, int y, int texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.isHighlighted = false;
        this.isSafe = true;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }

    public void setSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }

    public boolean isSafe() {
        return isSafe;
    }

    public void draw(GL gl) {
        gl.glPushMatrix();
        gl.glTranslatef(x * 50, y * 50, 0);
        if (isHighlighted) {
            gl.glColor3f(1.0f, 1.0f, 0.0f);
        } else if (!isSafe) {
            gl.glColor3f(1.0f, 0.0f, 0.0f);
        } else {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
        }

        if (texture != -1) {
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glBindTexture(GL.GL_TEXTURE_2D, texture);

            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(0, 0);
            gl.glTexCoord2f(1, 0); gl.glVertex2f(50, 0);
            gl.glTexCoord2f(1, 1); gl.glVertex2f(50, 50);
            gl.glTexCoord2f(0, 1); gl.glVertex2f(0, 50);
            gl.glEnd();

            gl.glDisable(GL.GL_TEXTURE_2D);
        } else {
            gl.glBegin(GL.GL_QUADS);
            gl.glVertex2f(0, 0);
            gl.glVertex2f(50, 0);
            gl.glVertex2f(50, 50);
            gl.glVertex2f(0, 50);
            gl.glEnd();
        }
        gl.glPopMatrix();
    }
}