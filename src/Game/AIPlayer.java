package Game;

import Entities.Tile;
import Utils.Collision;
import java.util.ArrayList;
import java.util.List;

public class AIPlayer {

    private List<Tile> solution = new ArrayList<Tile>();
    private final int BOARD_SIZE = 8;

    public List<Tile> getSolution() {
        return solution;
    }
    public boolean solve() {
        solution.clear();
        return solveRecursive(0);
    }

    private boolean solveRecursive(int col) {
        if (col >= BOARD_SIZE) {
            return true;
        }

        for (int row = 0; row < BOARD_SIZE; row++) {
            Tile currentQueen = new Tile(col, row, -1);

            if (isSafeToPlace(currentQueen)) {
                solution.add(currentQueen);
                if (solveRecursive(col + 1)) {
                    return true;
                }
                solution.remove(solution.size() - 1);
            }
        }
        return false;
    }

    private boolean isSafeToPlace(Tile newQueen) {
        for (Tile placedQueen : solution) {
            if (Collision.isAttacked(newQueen, solution)) {
                return false;
            }
        }
        return true;
    }
}