package fr.nicolas.wispy.ui.renderer_screens;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.Window;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameRenderer extends RendererScreen {

	public static final int BLOCK_RESOLUTION = 16;

	private int blockSize = BLOCK_RESOLUTION;

	private final BufferedImage skyTexture;

	private final Game game;
	private final Player player;
	private final Camera camera;
	private final WorldManager worldManager;

	public GameRenderer(Rectangle frameBounds, Game game) {
		super(frameBounds);

		this.game = game;
		this.player = game.getPlayer();
		this.camera = game.getCamera();
		this.worldManager = game.getWorldManager();

		addKeyListener(game);
		addMouseListener(game);
		addMouseMotionListener(game);
		setFocusable(true);
		setDoubleBuffered(true);

		this.skyTexture = Assets.get("map/sky");

		// Chunk loading thread
		game.getWorldManager().startLoadingChunkThread(game.getRunner(), this);

		setFrameBounds(frameBounds);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(skyTexture, 0, 0, this.getWidth(), this.getHeight(), null);

		worldManager.renderChunks(g, this.getWidth(), this.getHeight(), blockSize);

		int cameraX = (int) -((camera.getX() - player.getX()) * blockSize);
		int cameraY = (int) -((camera.getY() - player.getY()) * blockSize);

		g.translate(cameraX, cameraY);
		player.render(g, blockSize);
		g.translate(-cameraX, -cameraY);

		if (game.getMenu() != null) {
			game.getMenu().render(g, this.getWidth(), this.getHeight());
		} else {
			worldManager.renderSelections(g, blockSize, game.getMouseLocation());
		}
	}

	@Override
	public void setFrameBounds(Rectangle frameBounds) {
		blockSize = BLOCK_RESOLUTION * (int) frameBounds.getWidth() / Window.INIT_HEIGHT;
	}

	public int getBlockSize() {
		return blockSize;
	}

}
