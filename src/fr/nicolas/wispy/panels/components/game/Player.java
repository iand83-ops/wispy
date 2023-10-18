package fr.nicolas.wispy.panels.components.game;

import fr.nicolas.wispy.panels.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {

    private final BufferedImage playerStopImg;
    private final BufferedImage playerWalk1Img;
    private final BufferedImage playerWalk2Img;

    private boolean isFalling = false;
    private boolean isJumping = false;
    private boolean isWalking = false;
    private boolean isFacingRight = true;
    private boolean canGoLeft = true;
    private boolean canGoRight = true;
    private boolean canGoUp = true;

    private int jumpNum = 1;

    private final GamePanel gamePanel;

    private double prevX;
    private double prevY;
    private double x;
    private double y;

    private final double width;
    private final double height;

    private long walkTime = 0;

    public Player(BufferedImage playerStopImg, BufferedImage playerWalk1Img, BufferedImage playerWalk2Img, GamePanel gamePanel) {
        this.playerStopImg = playerStopImg;
        this.playerWalk1Img = playerWalk1Img;
        this.playerWalk2Img = playerWalk2Img;
        this.gamePanel = gamePanel;
        this.width = 1.0F;
        this.height = 2.0F;
    }

    public void tick(double elapsedTime) {
        gamePanel.getMapManager().computeCollisions(this);

        this.prevX = x;
        this.prevY = y;

        double moveSpeed = elapsedTime * 0.0035F;

        // Movements
        if (isWalking) {
            if (walkTime == 0) {
                walkTime = System.currentTimeMillis();
            }

            if (isFacingRight && canGoRight) {
                for (int i = 0; i < 2; i++) {
                    if (canGoRight) {
                        x += moveSpeed;
                        gamePanel.getMapManager().computeCollisions(this);
                    } else {
                        break;
                    }
                }
            }
            if (!isFacingRight && canGoLeft) {
                for (int i = 0; i < 2; i++) {
                    if (canGoLeft) {
                        x -= moveSpeed;
                        gamePanel.getMapManager().computeCollisions(this);
                    } else {
                        break;
                    }
                }
            }
        } else {
            walkTime = 0;
        }

        // Jump
        if (isJumping && canGoUp) {
            if (jumpNum != 15) {
                for (int i = 0; i < 8 - jumpNum / 2; i++) {
                    if (canGoUp) {
                        y -= moveSpeed;
                        gamePanel.getMapManager().computeCollisions(this);
                    } else {
                        break;
                    }
                }
                jumpNum++;
            } else {
                jumpNum = 1;
                isJumping = false;
            }
        }

        if (!canGoUp) {
            jumpNum = 1;
            isJumping = false;
        }

        // Gravity
        if (isFalling && !isJumping) {
            for (int i = 0; i < 4; i++) {
                if (isFalling) {
                    y += moveSpeed;
                    gamePanel.getMapManager().computeCollisions(this);
                } else {
                    break;
                }
            }
        }
    }

    public void render(Graphics g, int width, int height) {
        if (isJumping) {
            drawImage(g, playerWalk2Img, width, height);
        } else if (isFalling || !isWalking || !canGoRight || !canGoLeft) {
            drawImage(g, playerStopImg, width, height);
        } else {
            drawImage(g, (System.currentTimeMillis() - walkTime) % 320 < 160 ? playerWalk1Img : playerWalk2Img, width, height);
        }
    }

    private void drawImage(Graphics g, BufferedImage img, int width, int height) {
        if (isFacingRight) {
            g.drawImage(img, 0, -height, width, height, null);
        } else {
            g.drawImage(img, width, -height, -width, height, null);
        }
    }

    public void setFalling(boolean isFalling) {
        this.isFalling = isFalling;
    }

    public void setWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    public void setFacingRight(boolean isToRight) {
        this.isFacingRight = isToRight;
    }

    public void setJumping(boolean isJumping) {
        if (isJumping && !isFalling) {
            this.isJumping = true;
        }
    }

    public void setCanGoLeft(boolean canGoLeft) {
        this.canGoLeft = canGoLeft;
    }

    public void setCanGoRight(boolean canGoRight) {
        this.canGoRight = canGoRight;
    }

    public void setCanGoUp(boolean canGoUp) {
        this.canGoUp = canGoUp;
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }
}
