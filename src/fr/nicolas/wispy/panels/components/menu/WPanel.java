package fr.nicolas.wispy.panels.components.menu;

import java.awt.Rectangle;

import javax.swing.JPanel;

public abstract class WPanel extends JPanel {

	protected Rectangle frameBounds;

	public WPanel(Rectangle frameBounds) {
		this.frameBounds = frameBounds;
	}

	public void setFrameBounds(Rectangle frameBounds) {

	}

}
