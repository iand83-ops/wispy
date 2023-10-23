package fr.nicolas.wispy.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Rendering {

    default void drawImage(Graphics2D graphics, BufferedImage image, double x, double y, int width, int height) {
        int blockX = (int) Math.floor(x);
        int blockY = (int) Math.floor(y);

        graphics.translate(x - blockX, y - blockY);
        graphics.drawImage(image, blockX, blockY, width, height, null);
        graphics.translate(blockX - x, blockY - y);
    }

}
