package fr.nicolas.wispy.ui.renderer_screens;

import java.awt.Rectangle;

import javax.swing.JPanel;

public abstract class RendererScreen extends JPanel {

	protected Rectangle frameBounds;

	public RendererScreen(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}

	public abstract void resize(Rectangle frameBounds);

}
