package fr.nicolas.wispy.ui.renderer_screens;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.utils.MathUtils;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.IngameUI;
import fr.nicolas.wispy.ui.Window;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameRenderer extends RendererScreen {

	public static final int BLOCK_RESOLUTION = 16;

	private int blockSize = BLOCK_RESOLUTION;

	private final BufferedImage cloudTexture;

	private final Game game;
	private final Player player;
	private final Camera camera;
	private final WorldManager worldManager;
	private final IngameUI ingameUI;

	private Color skyColor = new Color(0);

	public GameRenderer(Rectangle frameBounds, Game game) {
		super(frameBounds);

		this.game = game;
		this.player = game.getPlayer();
		this.camera = game.getCamera();
		this.worldManager = game.getWorldManager();
		this.ingameUI = game.getIngameUI();

		addKeyListener(game);
		addMouseListener(game);
		addMouseMotionListener(game);
		addMouseWheelListener(game);
		setFocusable(true);
		setDoubleBuffered(true);

		this.cloudTexture = Assets.get("map/clouds");

		// Chunk loading thread
		game.getWorldManager().startLoadingChunkThread(game.getRunner(), this);

		resize(frameBounds);
	}

	@Override
	public void close() {
		removeKeyListener(game);
		removeMouseListener(game);
		removeMouseMotionListener(game);
		removeMouseWheelListener(game);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

		double worldCameraX = MathUtils.align(camera.getX());
		double worldCameraY = MathUtils.align(camera.getY());

		int dayColor = 0x2EA7E8;
		int nightColor = 0x151836;
		skyColor = MathUtils.interpolate(dayColor, nightColor, worldManager.getTimeFactor());
		graphics.setColor(skyColor);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.scale(blockSize, blockSize);
		graphics.translate(-worldCameraX, -worldCameraY);

		worldManager.render(graphics, this.getWidth() / (double) blockSize, this.getHeight() / (double) blockSize, false);

		player.render(graphics);

		worldManager.render(graphics, this.getWidth() / (double) blockSize, this.getHeight() / (double) blockSize, true);

		if (game.getMenu() == null) {
			worldManager.renderSelection(graphics, blockSize, game.getMouseLocation());
		}

		renderClouds(graphics, worldCameraX);

		graphics.translate(worldCameraX, worldCameraY);
		graphics.scale(1.0 / blockSize, 1.0 / blockSize);

		this.ingameUI.render(game, graphics, this.getWidth(), this.getHeight());

		if (game.getMenu() != null) {
			game.getMenu().render(graphics, this.getWidth(), this.getHeight());
		}
	}

	private void renderClouds(Graphics2D graphics, double worldCameraX) {
		int cloudsWidth = 128 / 2;
		int cloudsHeight = 16 / 2;
		double cloudsX = worldCameraX % cloudsWidth;
		int tilesX = (int) Math.ceil(getWidth() / (double) cloudsWidth) + 1;
		for (int x = 0; x < tilesX; x++) {
			graphics.translate(worldCameraX - cloudsX, 0);

			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8F));
			graphics.drawImage(cloudTexture, cloudsWidth * (int) Math.copySign(x, cloudsX), 35, cloudsWidth, cloudsHeight, null);
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0F));

			graphics.translate(-(worldCameraX - cloudsX), 0);
		}
	}

	@Override
	public void resize(Rectangle frameBounds) {
		blockSize = BLOCK_RESOLUTION * (int) frameBounds.getWidth() / Window.INIT_HEIGHT;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public Color getSkyColor() {
		return this.skyColor;
	}
}
