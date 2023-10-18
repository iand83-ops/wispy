package fr.nicolas.wispy.panels;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.frames.MainFrame;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.panels.components.game.Player;
import fr.nicolas.wispy.panels.components.menu.EscapeMenu;
import fr.nicolas.wispy.panels.components.menu.WPanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GamePanel extends WPanel implements KeyListener, MouseListener, MouseMotionListener {

	public static final int BLOCK_RESOLUTION = 16;

	private int blockSize = BLOCK_RESOLUTION;
	private int playerWidth;
	private int playerHeight;

	private final WorldManager mapManager;
	private final BufferedImage sky;
	private final Player player;
	private Camera camera;
	private Point mouseLocation;

	private boolean keyDPressed = false, keyQPressed = false, keySpacePressed = false, isEscapeMenuOpen = false;

	public GamePanel(Rectangle frameBounds, boolean isNewGame) {
		super(frameBounds);

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setFocusable(true);
		setDoubleBuffered(true);

		sky = Assets.get("map/sky");
		player = new Player(Assets.get("player/idle"),
				Assets.get("player/walk_1"),
				Assets.get("player/walk_2"), this);
		camera = new Camera();

		// Load world
		mapManager = new WorldManager(player, camera);
		mapManager.loadWorld("TestWorld");

		// Frame rate thread
		Runner runner = new Runner(this);
		// Chunk loading thread
		mapManager.startLoadingChunkThread(runner, this);

		setFrameBounds(frameBounds);
	}

	public void tick(double elapsedTime) {
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

		camera.setX(player.getX() - this.getWidth() / 2.0F / blockSize);
		camera.setY(player.getY() - this.getHeight() / 2.0F / blockSize);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(sky, 0, 0, this.getWidth(), this.getHeight(), null);
		// Le paint des blocs intégre le test de collision avec le joueur
		mapManager.renderChunks(g, this.getWidth(), this.getHeight(), blockSize);

		int cameraX = (int) -((camera.getX() - player.getX()) * blockSize);
		int cameraY = (int) -((camera.getY() - player.getY()) * blockSize);

		g.translate(cameraX, cameraY);
		player.render(g, playerWidth, playerHeight);
		g.translate(-cameraX, -cameraY);

		if (isEscapeMenuOpen) {
			new EscapeMenu().paint(g, this.getHeight());
		}

		if (mouseLocation != null) {
			mapManager.renderSelections(g, blockSize, mouseLocation);
		}
	}

	public void setFrameBounds(Rectangle frameBounds) {
		blockSize = BLOCK_RESOLUTION * (int) frameBounds.getWidth() / MainFrame.INIT_HEIGHT;
		playerWidth = (int) player.getWidth() * blockSize;
		playerHeight = (int) player.getHeight() * blockSize;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public Player getPlayer() {
		return player;
	}

	public WorldManager getMapManager() {
		return mapManager;
	}

	public Camera getCamera() {
		return camera;
	}

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

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_D) {
			keyDPressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			keyQPressed = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		this.mouseLocation = e.getPoint();
	}

	public void mouseMoved(MouseEvent e) {
		this.mouseLocation = e.getPoint();
	}

}
