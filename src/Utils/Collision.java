package Utils;

import Entities.Tile;
import java.util.List;

public class Collision {

    public static boolean isAttacked(Tile target, List<Tile> ducks) {
        for (Tile duck : ducks) {
            if (duck == target) continue;
            if (checkRow(target, duck) ||
                    checkColumn(target, duck) ||
                    checkDiagonal(target, duck)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isBoardSolved(List<Tile> ducks) {
        if (ducks.isEmpty()) return false;
        for (Tile duck : ducks) {
            if (isAttacked(duck, ducks)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkRow(Tile t1, Tile t2) {
        return t1.getY() == t2.getY();
    }

    private static boolean checkColumn(Tile t1, Tile t2) {
        return t1.getX() == t2.getX();
    }

    private static boolean checkDiagonal(Tile t1, Tile t2) {
        int dx = Math.abs(t1.getX() - t2.getX());
        int dy = Math.abs(t1.getY() - t2.getY());
        return dx == dy;
    }
}