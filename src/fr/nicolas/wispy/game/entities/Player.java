package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

public class Player extends Entity {

    protected boolean isFacingRightPrev = false;

    public Player(WorldManager worldManager) {
        super(worldManager, 1.0, 2.0);
        this.setIdleTextures(Assets.get("player/idle"));
        this.setWalkTextures(Assets.get("player/walk_1"), Assets.get("player/walk_2"));
        this.setJumpTextures(Assets.get("player/walk_2"));
    }

    @Override
    public void computeCollisions() {
        super.computeCollisions();

        if (!this.useHeightForFacing()) {
            this.persistentLiquidCollision = null;
        }
    }

    @Override
    public void tick(double elapsedTime) {
        super.tick(elapsedTime);

        if (useHeightForFacing()) {
            double degree90 = Math.toRadians(90);

            if (rotation == 0) {
                rotation = degree90;
            }

            if (isFacingRightPrev != isFacingRight) {
                isFacingRightPrev = isFacingRight;
                rotation = -(rotation - degree90);
                rotation += degree90;
            }

            double delta = Math.toRadians(elapsedTime * getMoveSpeed() * 20);

            if (prevY > y) {
                if (isFacingRight ? rotation > degree90 : rotation < degree90) {
                    delta *= 4;
                }

                rotation += delta * (isFacingRight ? -1 : 1);
            } else if (prevY < y) {
                if (isFacingRight ? rotation < degree90 : rotation > degree90) {
                    delta *= 4;
                }

                rotation -= delta * (isFacingRight ? -1 : 1);
            } else {
                if (rotation > degree90) {
                    rotation = Math.max(rotation - delta * 2, degree90);
                } else if (rotation < degree90) {
                    rotation = Math.min(rotation + delta * 2, degree90);
                }
            }
            rotation = Math.min(Math.max(rotation, degree90 - Math.toRadians(30)), degree90 + Math.toRadians(30));
        } else {
            if (isFacingRightPrev != isFacingRight) {
                isFacingRightPrev = isFacingRight;
            }
            rotation = 0;
        }
    }

    @Override
    public boolean useHeightForFacing() {
        return this.persistentLiquidCollision != null && (leftCollision == null || rightCollision == null || Math.abs(leftCollision.getX() - rightCollision.getX()) > 2);
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
        if (useHeightForFacing()) {
            return this.height;
        }
        return super.getCollisionWidth();
    }

    @Override
    public double getCollisionHeight() {
        if (useHeightForFacing()) {
            return this.width;
        }
        return super.getCollisionHeight();
    }

    @Override
    public boolean canGroundJump() {
        return this.groundCollision != null && !useHeightForFacing();
    }

}
