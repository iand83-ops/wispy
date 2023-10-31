package fr.nicolas.wispy.game;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.IngameUI;
import fr.nicolas.wispy.ui.Window;
import fr.nicolas.wispy.ui.menu.InventoryMenu;
import fr.nicolas.wispy.ui.menu.Menu;
import fr.nicolas.wispy.ui.menu.PauseMenu;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;
import fr.nicolas.wispy.ui.renderer_screens.MainMenuRenderer;
import fr.nicolas.wispy.ui.renderer_screens.RendererScreen;

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
    private Vector2D mouseLocation = new Vector2D(0, 0);

    private boolean rightKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean jumpKeyPressed = false;
    private boolean sprintKeyPressed = false;

    private boolean running = true;

    private boolean leftClickPressed = false;
    private boolean rightClickPressed = false;

    private Block selectedBlock = null;
    private int selectedBlockX = 0;
    private int selectedBlockY = 0;
    private long blockBreakStartTime = -1;

    private long lastGameTick = 0;
    private long gameTick = 0;

    private long lastLeftClickTick = 0;
    private long lastRightClickTick = 0;

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
        this.menu.close();
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

        if (menu == null) {
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
        } else {
            player.setWalking(false);
        }

        if (blockBreakStartTime == -1 && leftClickPressed && selectedBlock != null && selectedBlock.canBreak()) {
            blockBreakStartTime = System.currentTimeMillis();
        } else if (blockBreakStartTime != -1 && !leftClickPressed) {
            blockBreakStartTime = -1;
        }

        player.tick(elapsedTime);

        camera.setX(player.getX() - window.getWidth() / 2.0F / gameRenderer.getBlockSize());
        camera.setY(player.getY() - window.getHeight() / 2.0F / gameRenderer.getBlockSize());

        worldManager.gameTick(elapsedTime);

        ItemStack stack = player.getInventory().getItem(ingameUI.getSelectedSlot());
        if (stack != null) {
            if (rightClickPressed && selectedBlock != null && lastRightClickTick < gameTick) {
                lastRightClickTick = gameTick;
                stack.getItem().useItem(worldManager, stack, selectedBlock, selectedBlockX, selectedBlockY);
            }
        }
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

        if (menu != null) {
            menu.keyPressed(e);
            return;
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

        if (e.getKeyCode() == KeyEvent.VK_E) {
            if (menu == null) {
                this.openMenu(new InventoryMenu());
            } else if (menu instanceof InventoryMenu) {
                this.closeMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (menu != null) {
            menu.keyReleased(e);
        }

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
    public void keyTyped(KeyEvent e) {
        if (menu != null) {
            menu.keyTyped(e);
            return;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (menu != null) {
            menu.mouseClicked(e);
            return;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (menu != null) {
            menu.mouseEntered(e);
            return;
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (menu != null) {
            menu.mouseExited(e);
            return;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (menu != null) {
            menu.mousePressed(e);
            return;
        }

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
        if (menu != null) {
            menu.mouseReleased(e);
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClickPressed = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightClickPressed = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseLocation = new Vector2D(e.getPoint().getX(), e.getPoint().getY());

        if (menu != null) {
            menu.mouseDragged(e);
            return;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation = new Vector2D(e.getPoint().getX(), e.getPoint().getY());

        if (menu != null) {
            menu.mouseMoved(e);
            return;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (menu != null) {
            menu.mouseWheelMoved(e);
            return;
        }

        ingameUI.mouseWheelMoved(e);
    }

    public void start() {
        worldManager.loadWorld("TestWorld");
        gameRenderer = new GameRenderer(window.getBounds(), this);

        rendererScreen.close();
        rendererScreen = gameRenderer;

        window.setContentPane(rendererScreen);
        window.validate();
        rendererScreen.requestFocus();

        rendererScreen.resize(window.getBounds());
    }

    public void setSelectedBlock(Block selectedBlock, int x, int y) {
        this.selectedBlock = selectedBlock;
        this.selectedBlockX = x;
        this.selectedBlockY = y;
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

    public Vector2D getMouseLocation() {
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
