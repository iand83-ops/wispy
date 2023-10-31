package fr.nicolas.wispy.ui;

import fr.nicolas.wispy.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Window extends JFrame {

	private final Game game;
	public static final int INIT_WIDTH = 1250;
	public static final int INIT_HEIGHT = 720;

	public Window() {
		this.setTitle("Wispy");
		this.setSize(INIT_WIDTH, INIT_HEIGHT);
		this.setMinimumSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
		this.setLocationRelativeTo(null);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIgnoreRepaint(true);

		game = new Game(this);

		this.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				game.getRendererScreen().resize(getBounds());
			}

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}
		});

		this.setContentPane(game.getRendererScreen());

		this.setVisible(true);
	}

}
