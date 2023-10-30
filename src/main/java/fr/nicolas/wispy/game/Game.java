package fr.nicolas.wispy.game;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.IngameUI;
import fr.nicolas.wispy.ui.Window;
import fr.nicolas.wispy.ui.menu.Menu;
import fr.nicolas.wispy.ui.menu.PauseMenu;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;
import fr.nicolas.wispy.ui.renderer_screens.MainMenuRenderer;
import fr.nicolas.wispy.ui.renderer_screens.RendererScreen;

import java.awt.*;
import java.awt.event.*;


public class Game implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private static Game instance;

    private final Window window;
    private RendererScreen rendererScreen;
    private GameRenderer gameRenderer;

    private final WorldManager worldManager;
    private final Player player;
    private final Camera camera;
    private final IngameUI ingameUI;

    private final Runner runner;

    private Menu menu = null;
    private Point mouseLocation = new Point(0, 0);

    private boolean rightKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean jumpKeyPressed = false;
    private boolean sprintKeyPressed = false;

    private boolean running = true;

    private boolean leftClickPressed = false;
    private boolean rightClickPressed = false;

    private Block selectedBlock = null;
    private long blockBreakStartTime = -1;

    private long lastGameTick = 0;
    private long gameTick = 0;

    public Game(Window window) {
        instance = this;

        this.window = window;

        this.camera = new Camera();
        this.worldManager = new WorldManager(this, this.camera);
        this.player = new Player(this.worldManager);
        this.ingameUI = new IngameUI();

        this.rendererScreen = new MainMenuRenderer(window.getBounds(), this);

        // Frame rate thread
        this.runner = new Runner(this);

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.running = false;
            worldManager.closeWorld();
        }));
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

        if (System.currentTimeMillis() - lastGameTick >= 1000 / 20) {
            lastGameTick = System.currentTimeMillis();
            worldManager.gameTick(gameTick);
            gameTick++;
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

        if (blockBreakStartTime == -1 && leftClickPressed && selectedBlock != null && selectedBlock.canBreak()) {
            blockBreakStartTime = System.currentTimeMillis();
        } else if (blockBreakStartTime != -1 && !leftClickPressed) {
            blockBreakStartTime = -1;
        }

        ItemStack stack = player.getInventory().getItem(ingameUI.getSelectedSlot());
        if (stack != null) {
            if (rightClickPressed && selectedBlock != null) {
                int x = (int) Math.floor((double) mouseLocation.x / gameRenderer.getBlockSize() + camera.getX());
                int y = (int) Math.floor((double) mouseLocation.y / gameRenderer.getBlockSize() + camera.getY());

                stack.getItem().useItem(worldManager, stack, selectedBlock, x, y);
            }
        }

        player.tick(elapsedTime);

        camera.setX(player.getX() - window.getWidth() / 2.0F / gameRenderer.getBlockSize());
        camera.setY(player.getY() - window.getHeight() / 2.0F / gameRenderer.getBlockSize());

        worldManager.gameTick(elapsedTime);
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

        if (ingameUI.keyPressed(e)) {
            return;
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
    public void mousePressed(MouseEvent e) {
        if (ingameUI.mousePressed(e)) {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClickPressed = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightClickPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClickPressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightClickPressed = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseLocation = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = e.getPoint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        ingameUI.mouseWheelMoved(e);
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

    public void setSelectedBlock(Block selectedBlock) {
        this.selectedBlock = selectedBlock;
        this.blockBreakStartTime = -1;
    }

    public Block getSelectedBlock() {
        return this.selectedBlock;
    }

    public long getBlockBreakStartTime() {
        return this.blockBreakStartTime;
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

    public IngameUI getIngameUI() {
        return this.ingameUI;
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

    public long getGameTick() {
        return this.gameTick;
    }

}
