package fr.nicolas.wispy.ui.menu;

import java.awt.*;
import java.awt.event.*;

public abstract class Menu implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    protected int width;
    protected int height;

    public void render(Graphics2D graphics, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void close() {

    }

    public boolean doesPauseGame() {
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
