package fr.nicolas.wispy.ui.menu;

import java.awt.*;

public class EscapeMenu extends Menu {

	@Override
	public void render(Graphics graphics, int width, int height) {
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.fillRect(0, 0, 250, height);
		g2d.setColor(new Color(255, 255, 255, 200));
		g2d.setFont(new Font("Arial", Font.PLAIN, 40));
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.drawString("Options", 20, 55);
	}

}
