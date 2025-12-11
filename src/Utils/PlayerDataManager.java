package Utils;

import Game.Player;
import java.io.*;
import java.util.*;

/**
 * Manages saving and loading player data to/from files
 */
public class PlayerDataManager {

    private static final String SAVE_DIRECTORY = "src//PlayerData//";
    private static final String HIGH_SCORES_FILE = "high_scores.dat";
    private static final String PLAYERS_FILE = "players.dat";

    // Ensure save directory exists
    static {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Save player data after game ends
     */
    public static void savePlayer(Player player) {
        if (player == null) return;

        try {
            // Load existing players
            Map<String, PlayerData> players = loadAllPlayers();

            String username = player.getUsername();
            PlayerData data = players.getOrDefault(username, new PlayerData(username));

            // Update stats
            data.gamesPlayed++;
            data.totalScore += player.getScore();
            data.highScore = Math.max(data.highScore, player.getScore());
            data.totalLevels += player.getCurrentLevel();
            data.lastPlayed = new Date();

            if (player.hasWon()) {
                data.gamesWon++;
            }

            players.put(username, data);

            // Save to file
            saveAllPlayers(players);

            // Update high scores
            updateHighScores(player);

        } catch (Exception e) {
            System.err.println("Error saving player data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load all players from file
     */
    private static Map<String, PlayerData> loadAllPlayers() {
        Map<String, PlayerData> players = new HashMap<>();
        File file = new File(SAVE_DIRECTORY + PLAYERS_FILE);

        if (!file.exists()) {
            return players;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            players = (Map<String, PlayerData>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading players: " + e.getMessage());
        }

        return players;
    }

    /**
     * Save all players to file
     */
    private static void saveAllPlayers(Map<String, PlayerData> players) {
        File file = new File(SAVE_DIRECTORY + PLAYERS_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(players);
        } catch (Exception e) {
            System.err.println("Error saving players: " + e.getMessage());
        }
    }

    /**
     * Get player statistics
     */
    public static PlayerData getPlayerData(String username) {
        Map<String, PlayerData> players = loadAllPlayers();
        return players.getOrDefault(username, new PlayerData(username));
    }

    /**
     * Update high scores leaderboard
     */
    private static void updateHighScores(Player player) {
        List<HighScore> highScores = loadHighScores();

        HighScore newScore = new HighScore(
                player.getUsername(),
                player.getScore(),
                player.getCurrentLevel(),
                new Date()
        );

        highScores.add(newScore);

        // Sort by score descending
        Collections.sort(highScores, (a, b) -> Integer.compare(b.score, a.score));

        // Keep only top 10
        if (highScores.size() > 10) {
            highScores = highScores.subList(0, 10);
        }

        saveHighScores(highScores);
    }

    /**
     * Load high scores from file
     */
    public static List<HighScore> loadHighScores() {
        List<HighScore> scores = new ArrayList<>();
        File file = new File(SAVE_DIRECTORY + HIGH_SCORES_FILE);

        if (!file.exists()) {
            return scores;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            scores = (List<HighScore>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading high scores: " + e.getMessage());
        }

        return scores;
    }

    /**
     * Save high scores to file
     */
    private static void saveHighScores(List<HighScore> scores) {
        File file = new File(SAVE_DIRECTORY + HIGH_SCORES_FILE);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(scores);
        } catch (Exception e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    /**
     * Get top N high scores
     */
    public static List<HighScore> getTopScores(int n) {
        List<HighScore> scores = loadHighScores();
        return scores.size() > n ? scores.subList(0, n) : scores;
    }

    /**
     * Check if username exists
     */
    public static boolean usernameExists(String username) {
        Map<String, PlayerData> players = loadAllPlayers();
        return players.containsKey(username);
    }

    /**
     * Get all player names
     */
    public static List<String> getAllPlayerNames() {
        Map<String, PlayerData> players = loadAllPlayers();
        return new ArrayList<>(players.keySet());
    }

    /**
     * Delete player data
     */
    public static void deletePlayer(String username) {
        Map<String, PlayerData> players = loadAllPlayers();
        players.remove(username);
        saveAllPlayers(players);
    }

    /**
     * Clear all data (for testing)
     */
    public static void clearAllData() {
        File playersFile = new File(SAVE_DIRECTORY + PLAYERS_FILE);
        File scoresFile = new File(SAVE_DIRECTORY + HIGH_SCORES_FILE);

        playersFile.delete();
        scoresFile.delete();
    }

    // ==================== DATA CLASSES ====================

    /**
     * Player statistics data
     */
    public static class PlayerData implements Serializable {
        private static final long serialVersionUID = 1L;

        public String username;
        public int gamesPlayed;
        public int gamesWon;
        public int totalScore;
        public int highScore;
        public int totalLevels;
        public Date lastPlayed;
        public Date firstPlayed;

        public PlayerData(String username) {
            this.username = username;
            this.gamesPlayed = 0;
            this.gamesWon = 0;
            this.totalScore = 0;
            this.highScore = 0;
            this.totalLevels = 0;
            this.firstPlayed = new Date();
            this.lastPlayed = new Date();
        }

        public double getWinRate() {
            return gamesPlayed > 0 ? (gamesWon * 100.0 / gamesPlayed) : 0.0;
        }

        public double getAverageScore() {
            return gamesPlayed > 0 ? (totalScore * 1.0 / gamesPlayed) : 0.0;
        }

        @Override
        public String toString() {
            return String.format("%s - Games: %d, High Score: %d, Win Rate: %.1f%%",
                    username, gamesPlayed, highScore, getWinRate());
        }
    }

    /**
     * High score entry
     */
    public static class HighScore implements Serializable {
        private static final long serialVersionUID = 1L;

        public String username;
        public int score;
        public int level;
        public Date date;

        public HighScore(String username, int score, int level, Date date) {
            this.username = username;
            this.score = score;
            this.level = level;
            this.date = date;
        }

        @Override
        public String toString() {
            return String.format("%s - %d points (Level %d)", username, score, level);
        }
    }
}