package fr.nicolas.wispy.game;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.Window;
import fr.nicolas.wispy.ui.menu.Menu;
import fr.nicolas.wispy.ui.menu.PauseMenu;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;
import fr.nicolas.wispy.ui.renderer_screens.MainMenuRenderer;
import fr.nicolas.wispy.ui.renderer_screens.RendererScreen;

import java.awt.*;
import java.awt.event.*;


public class Game implements KeyListener, MouseListener, MouseMotionListener {

    private static Game instance;

    private final Window window;
    private RendererScreen rendererScreen;
    private GameRenderer gameRenderer;

    private final WorldManager worldManager;
    private final Player player;
    private final Camera camera;

    private final Runner runner;

    private Menu menu = null;
    private Point mouseLocation = new Point(0, 0);

    private boolean rightKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean jumpKeyPressed = false;
    private boolean sprintKeyPressed = false;

    private boolean running = true;

    public Game(Window window) {
        instance = this;

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

    public void closeMenu() {
        this.menu = null;
    }

    public void stop() {
        this.running = false;
        System.exit(0);
    }

    public void tick(double elapsedTime) {
        if (gameRenderer == null) {
            return;
        }

        if (rightKeyPressed) {
            player.setWalking(true);
            player.setFacingRight(true);
        }

        if (leftKeyPressed) {
            player.setWalking(true);
            player.setFacingRight(false);
        }

        if (!leftKeyPressed && !rightKeyPressed) {
            player.setWalking(false);
        }

        player.setSprinting(sprintKeyPressed);

        if (jumpKeyPressed) {
            player.jump();
        }

        player.tick(elapsedTime);

        camera.setX(player.getX() - window.getWidth() / 2.0F / gameRenderer.getBlockSize());
        camera.setY(player.getY() - window.getHeight() / 2.0F / gameRenderer.getBlockSize());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (menu == null) {
                this.openMenu(new PauseMenu());
            } else {
                this.closeMenu();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKeyPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKeyPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT) {
            sprintKeyPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            jumpKeyPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKeyPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKeyPressed = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_CONTROL || e.getKeyCode() == KeyEvent.VK_SHIFT) {
            sprintKeyPressed = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            jumpKeyPressed = false;
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

    public void start() {
        worldManager.loadWorld("TestWorld");
        gameRenderer = new GameRenderer(window.getBounds(), this);

        rendererScreen = gameRenderer;

        window.setContentPane(rendererScreen);
        window.validate();
        rendererScreen.requestFocus();

        rendererScreen.resize(window.getBounds());
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

    public boolean isPaused() {
        return this.menu != null && this.menu.doesPauseGame();
    }

    public boolean isRunning() {
        return this.running;
    }

    public static Game getInstance() {
        return Game.instance;
    }
}
