package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

public class Player extends Entity {

    public Player(WorldManager worldManager) {
        super(worldManager, 1.0, 2.0);
        this.setIdleTextures(Assets.get("player/idle"));
        this.setWalkTextures(Assets.get("player/walk_1"), Assets.get("player/walk_2"));
        this.setJumpTextures(Assets.get("player/walk_2"));
    }

    @Override
    public double getRotation() {
        if (persistentLiquidCollision != null) {
            return Math.toRadians(90);
        }

        return super.getRotation();
    }

    @Override
    public boolean useHeightForFacing() {
        return this.persistentLiquidCollision != null;
    }

    @Override
    public double getMoveSpeed() {
        double moveSpeed = 0.0035;

        if (persistentLiquidCollision != null) {
            moveSpeed *= 1.5;
        }

        return moveSpeed;
    }

    @Override
    public double getCollisionWidth() {
        if (persistentLiquidCollision != null) {
            return this.height;
        }
        return super.getCollisionWidth();
    }

    @Override
    public double getCollisionHeight() {
        if (persistentLiquidCollision != null) {
            return this.width;
        }
        return super.getCollisionHeight();
    }
}
