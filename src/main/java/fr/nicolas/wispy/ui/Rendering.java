package fr.nicolas.wispy.ui;

import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Rendering {

    default void drawImage(Graphics2D graphics, BufferedImage image, double x, double y, double width, double height) {
        int blockX = (int) Math.floor(x);
        int blockY = (int) Math.floor(y);

        graphics.translate(x - blockX, y - blockY);
        graphics.scale(1.0 / GameRenderer.BLOCK_RESOLUTION, 1.0 / GameRenderer.BLOCK_RESOLUTION);
        graphics.drawImage(image,
                blockX * GameRenderer.BLOCK_RESOLUTION,
                blockY * GameRenderer.BLOCK_RESOLUTION,
                (int) (width * GameRenderer.BLOCK_RESOLUTION),
                (int) (height * GameRenderer.BLOCK_RESOLUTION),
                null);
        graphics.scale(GameRenderer.BLOCK_RESOLUTION, GameRenderer.BLOCK_RESOLUTION);
        graphics.translate(blockX - x, blockY - y);
    }

}
