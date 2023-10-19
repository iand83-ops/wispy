package fr.nicolas.wispy.ui.menu;

import java.awt.*;

public abstract class Menu {

    public void render(Graphics2D graphics, int width, int height) {

    }

    public boolean doesPauseGame() {
        return true;
    }

}
