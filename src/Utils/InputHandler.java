package Utils;

import Entities.Tile;
import java.awt.event.*;
import java.util.List;

public class InputHandler implements MouseListener, MouseMotionListener {

    private List<Tile> ducks;
    private Tile selectedDuck = null;

    public InputHandler(List<Tile> ducks) {
        this.ducks = ducks;
    }

    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int gridX = mouseX / 50;
        int gridY = (600 - mouseY) / 50;

        for (Tile duck : ducks) {
            if (duck.getX() == gridX && duck.getY() == gridY) {
                selectedDuck = duck;
                duck.setHighlighted(true);
                break;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (selectedDuck != null) {
            int gridX = e.getX() / 50;
            int gridY = (600 - e.getY()) / 50;
            if (gridX >= 0 && gridX < 16 && gridY >= 0 && gridY < 8) {
                selectedDuck.setX(gridX);
                selectedDuck.setY(gridY);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (selectedDuck != null) {
            selectedDuck.setHighlighted(false);
            selectedDuck = null;
        }
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}