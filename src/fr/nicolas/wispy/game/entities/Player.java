package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    private final BufferedImage playerStopTexture;
    private final BufferedImage playerWalk1Texture;
    private final BufferedImage playerWalk2Texture;

    public Player(WorldManager worldManager) {
        super(worldManager, 1.0F, 2.0F);
        this.playerStopTexture = Assets.get("player/idle");
        this.playerWalk1Texture = Assets.get("player/walk_1");
        this.playerWalk2Texture = Assets.get("player/walk_2");
    }

    public void tick(double elapsedTime) {
        computeCollisions();

        prevX = x;
        prevY = y;

        double moveSpeed = elapsedTime * 0.0035F;

        // Walking
        if (isWalking) {
            if (walkTime == 0) {
                walkTime = System.currentTimeMillis();
            }

            if (isFacingRight && canGoRight) {
                x += moveSpeed;
            } else if (!isFacingRight && canGoLeft) {
                x -= moveSpeed;
            }
        } else {
            walkTime = 0;
        }

        // Jump
        if (isJumping && canGoUp) {
            if (jumpTime == 0) {
                jumpTime = System.currentTimeMillis();
                airTime = 0;
                isFalling = true;
            } else if (!isFalling && System.currentTimeMillis() - jumpTime > 100) {
                jumpTime = 0;
                isJumping = false;
            }
        } else {
            jumpTime = 0;
        }

        // Gravity
        if (isFalling) {
            if (airTime == 0) {
                airTime = System.currentTimeMillis();
            }
        } else {
            airTime = 0;
        }

        if (airTime != 0) {
            y += easeInCubic((System.currentTimeMillis() - airTime)) * moveSpeed / 40000.0F;
        }

        if (jumpTime != 0) {
            y -= moveSpeed * 3;
        }
    }

    public static double easeInCubic(double t) {
        return t * t;
    }

    public void render(Graphics g, int blockSize) {
        int width = (int) (this.width * blockSize);
        int height = (int) (this.height * blockSize);

        if (isJumping) {
            drawImage(g, playerWalk2Texture, width, height);
        } else if (isFalling || !isWalking || !canGoRight || !canGoLeft) {
            drawImage(g, playerStopTexture, width, height);
        } else {
            drawImage(g, (System.currentTimeMillis() - walkTime) % 320 < 160 ? playerWalk1Texture : playerWalk2Texture, width, height);
        }
    }

    private void drawImage(Graphics g, BufferedImage img, int width, int height) {
        if (isFacingRight) {
            g.drawImage(img, 0, -height, width, height, null);
        } else {
            g.drawImage(img, width, -height, -width, height, null);
        }
    }

}
