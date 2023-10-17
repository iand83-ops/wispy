package fr.nicolas.wispy.panels;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.frames.MainFrame;
import fr.nicolas.wispy.panels.components.game.Player;
import fr.nicolas.wispy.panels.components.menu.EscapeMenu;
import fr.nicolas.wispy.panels.components.menu.WPanel;
import fr.nicolas.wispy.panels.functions.MapManager;
import fr.nicolas.wispy.utils.Assets;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GamePanel extends WPanel implements KeyListener, MouseListener, MouseMotionListener {

	public static final int BLOCK_SIZE = 25;
	public static final int INIT_PLAYER_X = 605;
	public static final int INIT_PLAYER_Y = 315;

	private int newBlockWidth = BLOCK_SIZE;
	private int newBlockHeight = BLOCK_SIZE;
	private int playerX;
	private int playerY;
	private int playerWidth;
	private int playerHeight;

	private final MapManager mapManager;
	private final BufferedImage sky;
	private final Player player;
	private Point mouseLocation;

	private boolean keyDPressed = false, keyQPressed = false, keySpacePressed = false, isEscapeMenuOpen = false;

	public GamePanel(Rectangle frameBounds, boolean isNewGame) {
		super(frameBounds);

		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setFocusable(true);

		// Chargement des textures
		for (int i = 0; i < BlockID.values().length; i++) {
			loadBlockImage(BlockID.values()[i]);
		}

		sky = Assets.get("map/sky");
		player = new Player(Assets.get("player/idle"),
				Assets.get("player/walk_1"),
				Assets.get("player/walk_2"), this);

		// Création/Chargement nouveau monde
		mapManager = new MapManager(player);
		mapManager.loadWorld("TestWorld");

		// Lancement des threads
		Runner runner = new Runner(this); // Actualiser les blocs puis les textures
		mapManager.newLoadingMapThread(runner, this); // Charger et décharger les maps

		setFrameBounds(new Rectangle(MainFrame.INIT_WIDTH, MainFrame.INIT_HEIGHT));
	}

	// Gestion blocs (textures)
	public enum BlockID {
		STONE, DIRT, GRASS, SAND;

		private BufferedImage img = null;

		public void setImg(BufferedImage img) {
			this.img = img;
		}

		public BufferedImage getImg() {
			return img;
		}
	}

	private void loadBlockImage(BlockID blockID) {
		blockID.setImg(Assets.get("blocks/" + blockID.toString().toLowerCase()));
	}

	// Refresh / Paint methods
	public void refresh() {
		if (keyDPressed) {
			player.setWalking(true);
			player.setToRight(true);
		}

		if (keyQPressed) {
			player.setWalking(true);
			player.setToRight(false);
		}

		if (!keyQPressed && !keyDPressed) {
			player.setWalking(false);
		}

		if (keySpacePressed) {
			player.setJumping(true);
			keySpacePressed = false;
		}

		player.refresh(playerX, playerY, playerWidth, playerHeight);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(sky, 0, 0, this.getWidth(), this.getHeight(), null);
		// Le paint des blocs intégre le test de collision avec le joueur
		mapManager.drawMaps(g, this.getWidth(), this.getHeight(), newBlockWidth, newBlockHeight);
		mapManager.computeCollisions(
				this.getWidth(), this.getHeight(), newBlockWidth, newBlockHeight, playerWidth, playerHeight, playerX, playerY, this);

		player.paint(g, playerX, playerY, playerWidth, playerHeight);

		if (isEscapeMenuOpen) {
			new EscapeMenu().paint(g, this.getHeight());
		}

		mapManager.drawSelections(g, this.getWidth(), this.getHeight(), newBlockWidth, newBlockHeight, mouseLocation);
	}

	public void setFrameBounds(Rectangle frameBounds) {
		newBlockWidth = BLOCK_SIZE * (int) frameBounds.getWidth() / MainFrame.INIT_WIDTH;
		newBlockHeight = BLOCK_SIZE * (int) frameBounds.getHeight() / MainFrame.INIT_HEIGHT;
		playerX = INIT_PLAYER_X / GamePanel.BLOCK_SIZE * newBlockWidth;
		playerY = INIT_PLAYER_Y / GamePanel.BLOCK_SIZE * newBlockHeight;
		playerWidth = (int) player.getWidth() / GamePanel.BLOCK_SIZE * newBlockWidth;
		playerHeight = (int) player.getHeight() / GamePanel.BLOCK_SIZE * newBlockHeight;
	}

	// Getters and Setters
	public int getNewBlockWidth() {
		return newBlockWidth;
	}

	public int getNewBlockHeight() {
		return newBlockHeight;
	}

	public Player getPlayer() {
		return player;
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	// KeyListener
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

	// MouseListener

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

	// MouseMotionListener
	public void mouseDragged(MouseEvent e) {
		this.mouseLocation = e.getPoint();
	}

	public void mouseMoved(MouseEvent e) {
		this.mouseLocation = e.getPoint();
	}

}
