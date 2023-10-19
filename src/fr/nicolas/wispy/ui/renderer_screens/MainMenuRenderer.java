package fr.nicolas.wispy.ui.renderer_screens;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.ui.Window;
import fr.nicolas.wispy.ui.components.Button;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

public class MainMenuRenderer extends RendererScreen implements MouseListener, MouseMotionListener {

	private final BufferedImage backgroundTexture;
    private final BufferedImage title;

	private Point mouseLocation = new Point(0, 0);

	private Button buttonStart;
	private Button buttonSettings;
	private Button buttonQuit;

	private final Game game;

	public MainMenuRenderer(Rectangle frameBounds, Game game) {
		super(frameBounds);
		this.game = game;

		backgroundTexture = Assets.get("img/bg");
		title = Assets.get("img/title");

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setFocusable(true);

		init();
	}

	private void init() {
		this.setLayout(null);

		buttonStart = new Button("Start", new Rectangle((int) frameBounds.getWidth() / 2 - 450 / 2,
				(int) frameBounds.getHeight() / 2 - 93 - 110, 450, 93));
		buttonSettings = new Button("Settings", new Rectangle((int) frameBounds.getWidth() / 2 - 450 / 2,
				(int) frameBounds.getHeight() / 2 - 93, 450, 93));
		buttonQuit = new Button("Quit", new Rectangle((int) frameBounds.getWidth() / 2 - 450 / 2,
				(int) frameBounds.getHeight() / 2 - 93 - 110, 450, 93));

		add(buttonStart);
		add(buttonSettings);
		add(buttonQuit);

		setFrameBounds(frameBounds);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(backgroundTexture, 0, 0, this.getWidth(), this.getHeight(), null);
		g.setColor(new Color(0, 0, 0, 220));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		int newWidth = 393 * (int) frameBounds.getWidth() / Window.INIT_WIDTH;
		int newHeight = 142 * (int) frameBounds.getHeight() / Window.INIT_HEIGHT;
		g.drawImage(title, (int) frameBounds.getWidth() / 2 - newWidth / 2,
				(int) 55 * (int) frameBounds.getHeight() / Window.INIT_HEIGHT, newWidth, newHeight, null);
	}

	// MouseListener

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (buttonStart.mouseClick(mouseLocation)) {
				game.startGame();
			} else if (buttonSettings.mouseClick(mouseLocation)) {
				System.out.println(2);
			} else if (buttonQuit.mouseClick(mouseLocation)) {
				System.exit(0);
			}
		}

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.mouseLocation = e.getPoint();

		buttonStart.mouseMove(mouseLocation);
		buttonSettings.mouseMove(mouseLocation);
		buttonQuit.mouseMove(mouseLocation);

		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mouseLocation = e.getPoint();

		buttonStart.mouseMove(mouseLocation);
		buttonSettings.mouseMove(mouseLocation);
		buttonQuit.mouseMove(mouseLocation);

		repaint();
	}

	@Override
	public void setFrameBounds(Rectangle frameBounds) {
		this.frameBounds = frameBounds;

		int newWidth = 450 * (int) frameBounds.getWidth() / Window.INIT_WIDTH;
		int newHeight = 93 * (int) frameBounds.getHeight() / Window.INIT_HEIGHT;
		buttonStart.changeBounds(new Rectangle((int) frameBounds.getWidth() / 2 - newWidth / 2,
				(int) frameBounds.getHeight() / 2 - newHeight - 50 * (int) frameBounds.getHeight() / 700, newWidth,
				newHeight));

		buttonSettings.changeBounds(new Rectangle((int) frameBounds.getWidth() / 2 - newWidth / 2,
				(int) frameBounds.getHeight() / 2 - newHeight + 70 * (int) frameBounds.getHeight() / 700, newWidth,
				newHeight));

		buttonQuit.changeBounds(new Rectangle((int) frameBounds.getWidth() / 2 - newWidth / 2,
				(int) frameBounds.getHeight() / 2 - newHeight + 190 * (int) frameBounds.getHeight() / 700, newWidth,
				newHeight));

		buttonStart.reSize(40 * ((int) frameBounds.getWidth() * (int) frameBounds.getHeight()) / 1200000);
		buttonSettings.reSize(40 * ((int) frameBounds.getWidth() * (int) frameBounds.getHeight()) / 1200000);
		buttonQuit.reSize(40 * ((int) frameBounds.getWidth() * (int) frameBounds.getHeight()) / 1200000);
	}

}
