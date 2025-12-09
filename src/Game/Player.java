package Game;

import Entities.Players;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the 8-Queens Chess Game.
 * Manages player state, score, lives, and level progression.
 */
public class Player extends Players {

    // ==================== CORE ATTRIBUTES ====================
    private String username;
    private int score;
    private int lives;
    private int currentLevel;
    private long timeRemaining;  // Time remaining in milliseconds

    // ==================== GAME STATE ====================
    private int playerID;  // 1 = Player 1, 2 = Player 2, 0 = AI
    private boolean isActive;
    private boolean hasWon;
    private boolean hasLost;

    // ==================== PLAYER'S SOLDIERS ====================
    private Players[] soldiers;  // The 8 duck soldiers for this player
    private int soldiersPlaced;

    // ==================== STATISTICS ====================
    private int totalMoves;
    private int validPlacements;
    private int invalidPlacements;
    private long totalPlayTime;  // Total time played in milliseconds
    private long levelStartTime;

    // ==================== CONSTANTS ====================
    private static final int MAX_LIVES = 3;
    private static final int INITIAL_LIVES = 3;
    private static final int SOLDIERS_COUNT = 8;
    private static final int SCORE_PER_VALID_PLACEMENT = 100;
    private static final int SCORE_PER_LEVEL = 500;
    private static final int SCORE_PENALTY_INVALID = -50;
    private static final long DEFAULT_TIME_PER_LEVEL = 300000; // 5 minutes in milliseconds

    // ==================== CONSTRUCTORS ====================

    /**
     * Default constructor
     */
    public Player() {
        this("Player", 1);
    }

    /**
     * Constructor with username
     */
    public Player(String username) {
        this(username, 1);
    }

    /**
     * Constructor with username and player ID
     */
    public Player(String username, int playerID) {
        this.username = username;
        this.playerID = playerID;
        this.score = 0;
        this.lives = INITIAL_LIVES;
        this.currentLevel = 1;
        this.timeRemaining = DEFAULT_TIME_PER_LEVEL;
        this.isActive = true;
        this.hasWon = false;
        this.hasLost = false;
        this.soldiersPlaced = 0;
        this.totalMoves = 0;
        this.validPlacements = 0;
        this.invalidPlacements = 0;
        this.totalPlayTime = 0;
        this.levelStartTime = System.currentTimeMillis();

        initializeSoldiers();
    }

    // ==================== INITIALIZATION ====================

    /**
     * Initialize the player's 8 duck soldiers
     */
    private void initializeSoldiers() {
        soldiers = new Players[SOLDIERS_COUNT];
        for (int i = 0; i < SOLDIERS_COUNT; i++) {
            soldiers[i] = new Players(i + 1, playerID); // Duck types 1-8
        }
    }

    // ==================== LIFE MANAGEMENT ====================

    /**
     * Player loses a life
     */
    public void loseLife() {
        if (lives > 0) {
            lives--;

            if (lives <= 0) {
                gameOver();
            }
        }
    }

    /**
     * Add lives to player
     */
    public void addLife() {
        if (lives < MAX_LIVES) {
            lives++;
        }
    }

    /**
     * Reset lives to initial value
     */
    public void resetLives() {
        lives = INITIAL_LIVES;
    }

    /**
     * Check if player has lives remaining
     */
    public boolean hasLivesRemaining() {
        return lives > 0;
    }

    // ==================== SCORE MANAGEMENT ====================

    /**
     * Add score to player
     */
    public void addScore(int amount) {
        if (amount > 0) {
            score += amount;
        }
    }

    /**
     * Subtract score from player
     */
    public void subtractScore(int amount) {
        score -= amount;
        if (score < 0) {
            score = 0;
        }
    }

    /**
     * Award points for valid placement
     */
    public void awardValidPlacement() {
        addScore(SCORE_PER_VALID_PLACEMENT);
        validPlacements++;
    }

    /**
     * Penalize for invalid placement
     */
    public void penalizeInvalidPlacement() {
        subtractScore(Math.abs(SCORE_PENALTY_INVALID));
        invalidPlacements++;
    }

    /**
     * Reset score to zero
     */
    public void resetScore() {
        score = 0;
    }

    // ==================== LEVEL MANAGEMENT ====================

    /**
     * Advance to next level
     */
    public void nextLevel() {
        currentLevel++;
        addScore(SCORE_PER_LEVEL);

        // Reset level-specific state
        resetForNewLevel();
    }

    /**
     * Reset state for new level
     */
    private void resetForNewLevel() {
        soldiersPlaced = 0;
        timeRemaining = DEFAULT_TIME_PER_LEVEL;
        levelStartTime = System.currentTimeMillis();

        // Reset all soldiers
        for (Players soldier : soldiers) {
            if (soldier != null) {
                soldier.reset();
            }
        }
    }

    /**
     * Go to specific level
     */
    public void setLevel(int level) {
        this.currentLevel = level;
        resetForNewLevel();
    }

    // ==================== TIME MANAGEMENT ====================

    /**
     * Update time remaining (call each frame)
     */
    public void updateTime(long deltaTime) {
        if (isActive && timeRemaining > 0) {
            timeRemaining -= deltaTime;
            totalPlayTime += deltaTime;

            if (timeRemaining <= 0) {
                timeRemaining = 0;
                timeExpired();
            }
        }
    }

    /**
     * Called when time expires
     */
    private void timeExpired() {
        loseLife();
        if (hasLivesRemaining()) {
            resetForNewLevel();
        }
    }

    /**
     * Add bonus time
     */
    public void addTime(long milliseconds) {
        timeRemaining += milliseconds;
    }

    /**
     * Get time remaining in seconds
     */
    public int getTimeRemainingSeconds() {
        return (int)(timeRemaining / 1000);
    }

    /**
     * Get formatted time string (MM:SS)
     */
    public String getFormattedTime() {
        int totalSeconds = getTimeRemainingSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // ==================== SOLDIER MANAGEMENT ====================

    /**
     * Get a specific soldier
     */
    public Players getSoldier(int index) {
        if (index >= 0 && index < SOLDIERS_COUNT) {
            return soldiers[index];
        }
        return null;
    }

    /**
     * Get all soldiers
     */
    public Players[] getAllSoldiers() {
        return soldiers;
    }

    /**
     * Place a soldier on the board
     */
    public boolean placeSoldier(int soldierIndex, int row, int col) {
        if (soldierIndex < 0 || soldierIndex >= SOLDIERS_COUNT) {
            return false;
        }

        Players soldier = soldiers[soldierIndex];
        if (soldier == null || soldier.isPlaced()) {
            return false;
        }

        // Check if placement is valid
        if (soldier.wouldBeValidAt(row, col, soldiers)) {
            soldier.setPosition(row, col);
            soldiersPlaced++;
            totalMoves++;
            awardValidPlacement();

            // Check if all soldiers are placed (level complete)
            if (soldiersPlaced >= SOLDIERS_COUNT) {
                levelComplete();
            }

            return true;
        } else {
            totalMoves++;
            penalizeInvalidPlacement();
            return false;
        }
    }

    /**
     * Remove a soldier from the board
     */
    public void removeSoldier(int soldierIndex) {
        if (soldierIndex >= 0 && soldierIndex < SOLDIERS_COUNT) {
            Players soldier = soldiers[soldierIndex];
            if (soldier != null && soldier.isPlaced()) {
                soldier.removeFromBoard();
                soldiersPlaced--;
            }
        }
    }

    /**
     * Get number of soldiers placed
     */
    public int getSoldiersPlaced() {
        return soldiersPlaced;
    }
//
    /**
     * Check if all soldiers are placed
     */
    public boolean allSoldiersPlaced() {
        return soldiersPlaced >= SOLDIERS_COUNT;
    }

    // ==================== GAME STATE ====================

    /**
     * Level completed successfully
     */
    private void levelComplete() {
        // Award bonus points for remaining time
        int timeBonus = getTimeRemainingSeconds() * 10;
        addScore(timeBonus);

        // Check if game is won (after final level)
        if (isGameWon()) {
            winGame();
        }
    }

    /**
     * Check if player has won the game
     */
    private boolean isGameWon() {
        // Define win condition (e.g., complete level 10)
        return currentLevel >= 10 && allSoldiersPlaced();
    }

    /**
     * Player wins the game
     */
    private void winGame() {
        hasWon = true;
        isActive = false;
    }

    /**
     * Game over (lost all lives)
     */
    private void gameOver() {
        hasLost = true;
        isActive = false;
    }

    /**
     * Reset player to initial state
     */
    public void reset() {
        score = 0;
        lives = INITIAL_LIVES;
        currentLevel = 1;
        timeRemaining = DEFAULT_TIME_PER_LEVEL;
        isActive = true;
        hasWon = false;
        hasLost = false;
        soldiersPlaced = 0;
        totalMoves = 0;
        validPlacements = 0;
        invalidPlacements = 0;
        totalPlayTime = 0;
        levelStartTime = System.currentTimeMillis();

        // Reset all soldiers
        for (Players soldier : soldiers) {
            if (soldier != null) {
                soldier.reset();
            }
        }
    }

    /**
     * Update player state (call each frame)
     */
    public void update(long deltaTime) {
        updateTime(deltaTime);

        // Update all soldiers
        for (Players soldier : soldiers) {
            if (soldier != null) {
                soldier.update();
            }
        }
    }

    // ==================== STATISTICS ====================

    /**
     * Get success rate percentage
     */
    public double getSuccessRate() {
        if (totalMoves == 0) return 0.0;
        return (validPlacements * 100.0) / totalMoves;
    }

    /**
     * Get total play time in seconds
     */
    public int getTotalPlayTimeSeconds() {
        return (int)(totalPlayTime / 1000);
    }

    /**
     * Get formatted play time (HH:MM:SS)
     */
    public String getFormattedPlayTime() {
        int totalSeconds = getTotalPlayTimeSeconds();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // ==================== GETTERS & SETTERS ====================

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getLives() { return lives; }
    public void setLives(int lives) {
        this.lives = Math.max(0, Math.min(lives, MAX_LIVES));
    }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int level) { this.currentLevel = level; }

    public long getTimeRemaining() { return timeRemaining; }
    public void setTimeRemaining(long time) { this.timeRemaining = time; }

    public int getPlayerID() { return playerID; }
    public void setPlayerID(int id) { this.playerID = id; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public boolean hasWon() { return hasWon; }
    public boolean hasLost() { return hasLost; }

    public int getTotalMoves() { return totalMoves; }
    public int getValidPlacements() { return validPlacements; }
    public int getInvalidPlacements() { return invalidPlacements; }

    // ==================== UTILITY ====================

    /**
     * String representation for debugging
     */
    @Override
    public String toString() {
        return String.format("Player[name=%s, id=%d, score=%d, lives=%d, level=%d, soldiers=%d/8]",
                username, playerID, score, lives, currentLevel, soldiersPlaced);
    }

    /**
     * Clone player (for AI simulation)
     */
    public Player clone() {
        Player copy = new Player(this.username, this.playerID);
        copy.score = this.score;
        copy.lives = this.lives;
        copy.currentLevel = this.currentLevel;
        copy.timeRemaining = this.timeRemaining;
        copy.isActive = this.isActive;
        copy.hasWon = this.hasWon;
        copy.hasLost = this.hasLost;
        copy.soldiersPlaced = this.soldiersPlaced;
        return copy;
    }
}