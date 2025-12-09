package Entities;

import Texture.TextureReader;
import javax.media.opengl.GL;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a duck soldier piece in the 8-Queens Chess Game.
 * Each soldier follows chess queen movement rules.
 */
public class Players {

    // ==================== POSITION ATTRIBUTES ====================
    private float x;              // Screen X coordinate
    private float y;              // Screen Y coordinate
    private int row;              // Board row (0-7)
    private int col;              // Board column (0-7)

    // ==================== VISUAL ATTRIBUTES ====================
    private Animation animation;
    private boolean isSelected;
    private boolean isHighlighted;
    private int textureID;        // OpenGL texture ID (if needed)

    // ==================== GAME STATE ====================
    private boolean isPlaced;     // Whether soldier is on board
    private int playerID;         // Owner (1 = P1, 2 = P2, 0 = AI)
    private int duckType;         // Which duck (1-8 for D1-D8)

    // ==================== RENDERING CONSTANTS ====================
    private static final float SOLDIER_SIZE = 0.8f; // Relative to tile size

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor - creates soldier at origin
     */
    public Players() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.row = -1;  // Not on board
        this.col = -1;
        this.isPlaced = false;
        this.isSelected = false;
        this.isHighlighted = false;
        this.playerID = 1;
        this.duckType = 1; // Default to D1
        this.animation = new Animation();
    }

    /**
     * Constructor with duck type
     * @param duckType Which duck (1-8)
     * @param playerID Which player owns this duck
     */
    public Players(int duckType, int playerID) {
        this();
        this.duckType = duckType;
        this.playerID = playerID;
        loadDuckTextures();
    }

    /**
     * Constructor with initial position
     */
    public Players(float x, float y, int duckType) {
        this();
        this.x = x;
        this.y = y;
        this.duckType = duckType;
        this.animation.setPosition(x, y);
        loadDuckTextures();
    }

    // ==================== TEXTURE LOADING ====================

    /**
     * Load textures for this duck soldier
     */
    public void loadDuckTextures() {
        String duckFolder = getDuckFolderPath();
        animation.loadDuckAnimations(duckFolder);
    }

    /**
     * Get the folder path for this duck's textures
     */
    private String getDuckFolderPath() {
        return "Assets//Players//D" + duckType;
    }

    // ==================== CORE METHODS ====================

    /**
     * Render the soldier sprite at current position
     */
    public void draw(GL gl) {
        if (!isPlaced && !animation.isMoving()) {
            return; // Don't draw if not visible
        }

        // Get current position from animation
        float renderX = animation.getCurrentX();
        float renderY = animation.getCurrentY();

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Draw selection/highlight glow first (behind soldier)
        if (isSelected || isHighlighted) {
            drawGlow(gl, renderX, renderY);
        }

        // Get current animation frame
        TextureReader.Texture currentFrame = animation.getCurrentFrame();

        if (currentFrame != null) {
            drawTexturedQuad(gl, currentFrame, renderX, renderY);
        } else {
            // Fallback: draw colored circle if no texture
            drawCircle(gl, renderX, renderY, SOLDIER_SIZE / 2, getPlayerColor());
        }

        gl.glDisable(GL.GL_BLEND);
    }

    /**
     * Draw textured quad with current animation frame
     */
    private void drawTexturedQuad(GL gl, TextureReader.Texture texture, float x, float y) {
        gl.glEnable(GL.GL_TEXTURE_2D);

        // Create OpenGL texture if needed
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

        // Set texture parameters
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        // Upload texture data
        gl.glTexImage2D(
                GL.GL_TEXTURE_2D,
                0,
                GL.GL_RGBA,
                texture.getWidth(),
                texture.getHeight(),
                0,
                GL.GL_RGBA,
                GL.GL_UNSIGNED_BYTE,
                texture.getPixels()
        );

        // Draw quad
        float size = SOLDIER_SIZE;
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0, 1); gl.glVertex2f(x - size/2, y - size/2);
        gl.glTexCoord2f(1, 1); gl.glVertex2f(x + size/2, y - size/2);
        gl.glTexCoord2f(1, 0); gl.glVertex2f(x + size/2, y + size/2);
        gl.glTexCoord2f(0, 0); gl.glVertex2f(x - size/2, y + size/2);
        gl.glEnd();

        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDeleteTextures(1, textures, 0);
    }

    /**
     * Update soldier state each frame
     */
    public void update() {
        // Update animation
        if (animation != null) {
            animation.update();
        }

        // Sync position with animation if moving
        if (animation.isMoving()) {
            x = animation.getCurrentX();
            y = animation.getCurrentY();
        }

        // Reset to idle when movement animation completes
        if (animation.isComplete() && !isSelected) {
            animation.setAnimationType(Animation.AnimationType.IDLE);
        }
    }

    // ==================== POSITION METHODS ====================

    /**
     * Set soldier position on board (with animation)
     */
    public void setPosition(int row, int col) {
        setPosition(row, col, true);
    }

    /**
     * Set soldier position on board
     * @param animated If true, animate movement to position
     */
    public void setPosition(int row, int col, boolean animated) {
        this.row = row;
        this.col = col;
        this.isPlaced = true;

        // Calculate target screen coordinates
        float targetX = boardToScreenX(col);
        float targetY = boardToScreenY(row);

        if (animated) {
            animation.moveTo(targetX, targetY);
        } else {
            animation.setPosition(targetX, targetY);
            x = targetX;
            y = targetY;
        }
    }

    /**
     * Move soldier to new position
     */
    public void moveTo(int row, int col, boolean animated) {
        setPosition(row, col, animated);
    }

    /**
     * Get current board position
     */
    public Point getPosition() {
        return new Point(row, col);
    }

    /**
     * Remove soldier from board
     */
    public void removeFromBoard() {
        this.isPlaced = false;
        this.row = -1;
        this.col = -1;
    }

    /**
     * Convert board column to screen X coordinate
     */
    private float boardToScreenX(int col) {
        // Adjust these values based on your board rendering
        float boardOriginX = -3.5f;
        float tileSize = 1.0f;
        return boardOriginX + col * tileSize + tileSize / 2;
    }

    /**
     * Convert board row to screen Y coordinate
     */
    private float boardToScreenY(int row) {
        // Adjust these values based on your board rendering
        float boardOriginY = -3.5f;
        float tileSize = 1.0f;
        return boardOriginY + row * tileSize + tileSize / 2;
    }

    // ==================== COLLISION DETECTION ====================

    /**
     * Check if this soldier is attacking another soldier (8-Queens rules)
     */
    public boolean isAttacking(Players other) {
        if (!this.isPlaced || !other.isPlaced) return false;
        if (this == other) return false;

        // Same row
        if (this.row == other.row) return true;

        // Same column
        if (this.col == other.col) return true;

        // Same diagonal
        int rowDiff = Math.abs(this.row - other.row);
        int colDiff = Math.abs(this.col - other.col);
        if (rowDiff == colDiff) return true;

        return false;
    }

    /**
     * Get all tiles threatened by this soldier
     */
    public List<Point> getThreatenedTiles() {
        List<Point> threatened = new ArrayList<>();

        if (!isPlaced) return threatened;

        // Add all tiles in the same row
        for (int c = 0; c < 8; c++) {
            if (c != col) {
                threatened.add(new Point(row, c));
            }
        }

        // Add all tiles in the same column
        for (int r = 0; r < 8; r++) {
            if (r != row) {
                threatened.add(new Point(r, col));
            }
        }

        // Add diagonal tiles (all four diagonals)
        addDiagonalTiles(threatened);

        return threatened;
    }

    /**
     * Helper method to add diagonal tiles
     */
    private void addDiagonalTiles(List<Point> tiles) {
        // Up-Right diagonal
        for (int i = 1; i < 8; i++) {
            int r = row + i;
            int c = col + i;
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                tiles.add(new Point(r, c));
            }
        }

        // Up-Left diagonal
        for (int i = 1; i < 8; i++) {
            int r = row + i;
            int c = col - i;
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                tiles.add(new Point(r, c));
            }
        }

        // Down-Right diagonal
        for (int i = 1; i < 8; i++) {
            int r = row - i;
            int c = col + i;
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                tiles.add(new Point(r, c));
            }
        }

        // Down-Left diagonal
        for (int i = 1; i < 8; i++) {
            int r = row - i;
            int c = col - i;
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                tiles.add(new Point(r, c));
            }
        }
    }

    /**
     * Check if placement is valid (not attacking any other soldier)
     */
    public boolean isValidPlacement(Players[] allSoldiers) {
        if (!isPlaced) return false;

        for (Players other : allSoldiers) {
            if (other == this || !other.isPlaced) continue;
            if (this.isAttacking(other)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a specific position would be valid
     */
    public boolean wouldBeValidAt(int row, int col, Players[] allSoldiers) {
        int oldRow = this.row;
        int oldCol = this.col;
        boolean wasPlaced = this.isPlaced;

        this.row = row;
        this.col = col;
        this.isPlaced = true;

        boolean valid = isValidPlacement(allSoldiers);

        this.row = oldRow;
        this.col = oldCol;
        this.isPlaced = wasPlaced;

        return valid;
    }

    // ==================== VISUAL STATE METHODS ====================

    /**
     * Set selection state
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            animation.setAnimationType(Animation.AnimationType.SELECTED);
        } else {
            animation.setAnimationType(Animation.AnimationType.IDLE);
        }
    }

    /**
     * Set highlight state (for conflicts)
     */
    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }

    /**
     * Draw selection/highlight glow
     */
    private void drawGlow(GL gl, float x, float y) {
        gl.glDisable(GL.GL_TEXTURE_2D);

        float[] color;
        if (isSelected) {
            color = new float[]{1.0f, 1.0f, 0.0f, 0.6f}; // Yellow
        } else if (isHighlighted) {
            color = new float[]{1.0f, 0.0f, 0.0f, 0.6f}; // Red (conflict)
        } else {
            color = new float[]{0.0f, 1.0f, 0.0f, 0.6f}; // Green
        }

        drawCircle(gl, x, y, SOLDIER_SIZE * 0.7f, color);
    }

    /**
     * Draw a circle (for glow or fallback rendering)
     */
    private void drawCircle(GL gl, float cx, float cy, float radius, float[] color) {
        gl.glColor4f(color[0], color[1], color[2], color[3]);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        gl.glVertex2f(cx, cy); // Center
        for (int i = 0; i <= 32; i++) {
            float angle = (float)(i * 2 * Math.PI / 32);
            float x = cx + radius * (float)Math.cos(angle);
            float y = cy + radius * (float)Math.sin(angle);
            gl.glVertex2f(x, y);
        }
        gl.glEnd();
    }

    /**
     * Get color based on player ID
     */
    private float[] getPlayerColor() {
        switch (playerID) {
            case 1: return new float[]{0.2f, 0.6f, 1.0f, 1.0f}; // Blue
            case 2: return new float[]{1.0f, 0.4f, 0.2f, 1.0f}; // Red
            default: return new float[]{0.5f, 0.5f, 0.5f, 1.0f}; // Gray (AI)
        }
    }

    // ==================== GETTERS & SETTERS ====================

    public float getX() { return x; }
    public void setX(float x) {
        this.x = x;
        this.animation.setPosition(x, y);
    }

    public float getY() { return y; }
    public void setY(float y) {
        this.y = y;
        this.animation.setPosition(x, y);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public boolean isPlaced() { return isPlaced; }
    public void setPlaced(boolean placed) { this.isPlaced = placed; }

    public boolean isSelected() { return isSelected; }
    public boolean isHighlighted() { return isHighlighted; }

    public boolean isMoving() { return animation.isMoving(); }

    public int getPlayerID() { return playerID; }
    public void setPlayerID(int playerID) { this.playerID = playerID; }

    public int getDuckType() { return duckType; }
    public void setDuckType(int duckType) {
        this.duckType = duckType;
        loadDuckTextures();
    }

    public Animation getAnimation() { return animation; }
    public void setAnimation(Animation animation) { this.animation = animation; }

    public int getTextureID() { return textureID; }
    public void setTextureID(int textureID) { this.textureID = textureID; }

    // ==================== UTILITY METHODS ====================

    /**
     * Reset soldier to default state
     */
    public void reset() {
        this.row = -1;
        this.col = -1;
        this.isPlaced = false;
        this.isSelected = false;
        this.isHighlighted = false;
        this.animation.stop();
        this.animation.setAnimationType(Animation.AnimationType.IDLE);
    }

    /**
     * Clone this soldier (for AI simulation)
     */
    public Players clone() {
        Players copy = new Players(this.duckType, this.playerID);
        copy.x = this.x;
        copy.y = this.y;
        copy.row = this.row;
        copy.col = this.col;
        copy.isPlaced = this.isPlaced;
        return copy;
    }

    /**
     * String representation for debugging
     */
    @Override
    public String toString() {
        return String.format("Duck%d[Player=%d, Pos=(%d,%d), Placed=%b]",
                duckType, playerID, row, col, isPlaced);
    }
}