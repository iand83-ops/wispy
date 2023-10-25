package fr.nicolas.wispy.game.utils;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.render.Vector2D;

import java.awt.*;

public class MathUtils {

    public static double easeIn(double t) {
        return t * t;
    }

    public static Vector2D rotatePoint(Vector2D point, Vector2D center, double rotation) {
        double x = center.x + (point.x - center.x) * Math.cos(rotation) - (point.y - center.y) * Math.sin(rotation);
        double y = center.y + (point.x - center.x) * Math.sin(rotation) + (point.y - center.y) * Math.cos(rotation);
        return new Vector2D(x, y);
    }

    public static Color interpolate(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) (r1 + ratio * (r2 - r1));
        int g = (int) (g1 + ratio * (g2 - g1));
        int b = (int) (b1 + ratio * (b2 - b1));

        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }

    public static double align(double value) {
        double blockSize = Game.getInstance().getGameRenderer().getBlockSize();
        return (int) (value * blockSize) / blockSize;
    }

}
