package fr.nicolas.wispy.game.utils;

import fr.nicolas.wispy.game.render.Vector2D;

public class MathUtils {

    public static double easeIn(double t) {
        return t * t;
    }

    public static Vector2D rotatePoint(Vector2D point, Vector2D center, double rotation) {
        double x = center.x + (point.x - center.x) * Math.cos(rotation) - (point.y - center.y) * Math.sin(rotation);
        double y = center.y + (point.x - center.x) * Math.sin(rotation) + (point.y - center.y) * Math.cos(rotation);
        return new Vector2D(x, y);
    }

}
