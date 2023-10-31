package fr.nicolas.wispy.ui.renderer_screens;

import java.awt.Rectangle;

import javax.swing.JPanel;

public abstract class RendererScreen extends JPanel {

	protected Rectangle frameBounds;

	public RendererScreen(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
		this.setIgnoreRepaint(true);
	}

	public void close() {

	}

	public abstract void resize(Rectangle frameBounds);

}
