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

		Graphics2D graphics = (Graphics2D) g;

		graphics.drawImage(skyTexture, 0, 0, this.getWidth(), this.getHeight(), null);

		double worldCameraX = camera.getX() - Math.floor(camera.getX());
		double worldCameraY = camera.getY() - Math.floor(camera.getY());

		double playerCameraX = camera.getX() - player.getX();
		double playerCameraY = camera.getY() - player.getY();

		graphics.scale(blockSize, blockSize);

		graphics.translate(-worldCameraX, -worldCameraY);
		worldManager.renderChunks(graphics, this.getWidth() / (double) blockSize, this.getHeight() / (double) blockSize);
		graphics.translate(worldCameraX, worldCameraY);

		graphics.translate(-playerCameraX, -playerCameraY);
		player.render(graphics);
		graphics.translate(playerCameraX, playerCameraY);

		if (game.getMenu() != null) {
			graphics.scale(1.0 / blockSize, 1.0 / blockSize);

			game.getMenu().render(graphics, this.getWidth(), this.getHeight());
		} else {
			graphics.translate(-worldCameraX, -worldCameraY);
			worldManager.renderSelection(graphics, blockSize, game.getMouseLocation());
			graphics.translate(worldCameraX, worldCameraY);

			graphics.scale(1.0 / blockSize, 1.0 / blockSize);
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
