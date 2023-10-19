package fr.nicolas.wispy.game;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.Window;
import fr.nicolas.wispy.ui.menu.Menu;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;
import fr.nicolas.wispy.ui.renderer_screens.MainMenuRenderer;
import fr.nicolas.wispy.ui.renderer_screens.RendererScreen;

import java.awt.*;
import java.awt.event.*;


public class Game implements KeyListener, MouseListener, MouseMotionListener {

    private final Window window;
    private RendererScreen rendererScreen;
    private GameRenderer gameRenderer;

    private final WorldManager worldManager;
    private final Player player;
    private final Camera camera;

    private Runner runner;

    private Menu menu = null;
    private Point mouseLocation = new Point(0, 0);

    private boolean keyDPressed = false;
    private boolean keyQPressed = false;
    private boolean keySpacePressed = false;
    private boolean isEscapeMenuOpen = false;

    public Game(Window window) {
        this.window = window;

        this.camera = new Camera();
        this.worldManager = new WorldManager(this, this.camera);
        this.player = new Player(this.worldManager);

        this.rendererScreen = new MainMenuRenderer(window.getBounds(), this);

        // Frame rate thread
        this.runner = new Runner(this);
    }

    public void openMenu(Menu menu) {
        this.menu = menu;
    }

    public void tick(double elapsedTime) {
        if (gameRenderer == null) {
            return;
        }

        if (keyDPressed) {
            player.setWalking(true);
            player.setFacingRight(true);
        }

        if (keyQPressed) {
            player.setWalking(true);
            player.setFacingRight(false);
        }

        if (!keyQPressed && !keyDPressed) {
            player.setWalking(false);
        }

        if (keySpacePressed) {
            player.setJumping(true);
            keySpacePressed = false;
        }

        player.tick(elapsedTime);

        camera.setX(player.getX() - window.getWidth() / 2.0F / gameRenderer.getBlockSize());
        camera.setY(player.getY() - window.getHeight() / 2.0F / gameRenderer.getBlockSize());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            isEscapeMenuOpen = !isEscapeMenuOpen;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            keyDPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            keyQPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            keySpacePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            keyDPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            keyQPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseLocation = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
    }

    public void startGame() {
        worldManager.loadWorld("TestWorld");
        gameRenderer = new GameRenderer(window.getBounds(), this);

        rendererScreen = gameRenderer;

        window.setContentPane(rendererScreen);
        window.validate();
        rendererScreen.requestFocus();

        rendererScreen.setFrameBounds(window.getBounds());
    }

    public Player getPlayer() {
        return this.player;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public GameRenderer getGameRenderer() {
        return this.gameRenderer;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    public void setRendererScreen(RendererScreen rendererScreen) {
        this.rendererScreen = rendererScreen;
    }

    public RendererScreen getRendererScreen() {
        return this.rendererScreen;
    }

    public Window getWindow() {
        return this.window;
    }

    public Point getMouseLocation() {
        return this.mouseLocation;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public Runner getRunner() {
        return this.runner;
    }
}
