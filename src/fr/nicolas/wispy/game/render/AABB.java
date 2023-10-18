package fr.nicolas.wispy.game.render;

public class AABB {

    private final Vector2D min;
    private final Vector2D max;

    public AABB(Vector2D min, Vector2D max) {
        this.min = min;
        this.max = max;
    }

    public Vector2D getMin() {
        return min;
    }

    public Vector2D getMax() {
        return max;
    }

    public boolean intersects(AABB other) {
        if (this.min.x > other.max.x || this.max.x < other.min.x) {
            return false;
        }
        return !(this.min.y > other.max.y) && !(this.max.y < other.min.y);
    }

    public boolean contains(Vector2D point) {
        return point.x >= min.x && point.x <= max.x && point.y >= min.y && point.y <= max.y;
    }
}