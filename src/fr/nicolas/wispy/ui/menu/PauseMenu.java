package fr.nicolas.wispy.ui.menu;

import java.awt.*;

public class PauseMenu extends Menu {

	@Override
	public void render(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(0, 0, 0, 200));
		graphics.fillRect(0, 0, 250, height);
		graphics.setColor(new Color(255, 255, 255, 200));
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		graphics.drawString("Options", 20, 55);
	}

}
