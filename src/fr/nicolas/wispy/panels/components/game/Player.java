package fr.nicolas.wispy.panels.components.game;

import fr.nicolas.wispy.panels.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Rectangle {

    private final BufferedImage playerStopImg;
    private final BufferedImage playerWalk1Img;
    private final BufferedImage playerWalk2Img;

    private boolean isFalling = false;
    private boolean isJumping = false;
    private boolean isWalking = false;
    private boolean isToRight = true;
    private boolean canGoLeft = true;
    private boolean canGoRight = true;
    private boolean canGoUp = true;

    private int jumpNum = 1;
    private int walkNum = 0;

    private final GamePanel gamePanel;

    public Player(BufferedImage playerStopImg, BufferedImage playerWalk1Img, BufferedImage playerWalk2Img, GamePanel gamePanel) {
        this.playerStopImg = playerStopImg;
        this.playerWalk1Img = playerWalk1Img;
        this.playerWalk2Img = playerWalk2Img;
        this.gamePanel = gamePanel;
        this.width = GamePanel.BLOCK_SIZE;
        this.height = GamePanel.BLOCK_SIZE * 2;
    }

    public void refresh(int playerWidth, int playerHeight, int playerX, int playerY) {
        gamePanel.getMapManager().computeCollisions(gamePanel.getWidth(), gamePanel.getHeight(),
                gamePanel.getNewBlockWidth(), gamePanel.getNewBlockHeight(), playerWidth, playerHeight,
                playerX, playerY, gamePanel);

        // Déplacements
        if (isWalking) {
            if (isToRight && canGoRight) {
                walkNum++;
                for (int i = 0; i < 2; i++) {
                    if (canGoRight) {
                        x += 1;
                        gamePanel.getMapManager().computeCollisions(
                                gamePanel.getWidth(), gamePanel.getHeight(), gamePanel.getNewBlockWidth(),
                                gamePanel.getNewBlockHeight(), playerWidth, playerHeight, playerX, playerY, gamePanel);
                    } else {
                        break;
                    }
                }
            }
            if (!isToRight && canGoLeft) {
                walkNum++;
                for (int i = 0; i < 2; i++) {
                    if (canGoLeft) {
                        x -= 1;
                        gamePanel.getMapManager().computeCollisions(
                                gamePanel.getWidth(), gamePanel.getHeight(), gamePanel.getNewBlockWidth(),
                                gamePanel.getNewBlockHeight(), playerWidth, playerHeight, playerX, playerY, gamePanel);
                    } else {
                        break;
                    }
                }
            }
        }

        if (walkNum > 20) {
            walkNum = 1;
        }

        // Jump
        if (isJumping && canGoUp) {
            if (jumpNum != 15) {
                for (int i = 0; i < 8 - jumpNum / 2; i++) {
                    if (canGoUp) {
                        y -= 1;
                        gamePanel.getMapManager().computeCollisions(
                                gamePanel.getWidth(), gamePanel.getHeight(), gamePanel.getNewBlockWidth(),
                                gamePanel.getNewBlockHeight(), playerWidth, playerHeight, playerX, playerY, gamePanel);
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

        // Gravité
        if (isFalling && !isJumping) {
            for (int i = 0; i < 4; i++) {
                if (isFalling) {
                    y += 1;
                    gamePanel.getMapManager().computeCollisions(
                            gamePanel.getWidth(), gamePanel.getHeight(), gamePanel.getNewBlockWidth(),
                            gamePanel.getNewBlockHeight(), playerWidth, playerHeight, playerX, playerY, gamePanel);
                } else {
                    break;
                }
            }
        }

    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        // TODO: iswalking inutilisable car toujours faux donc frame playerStopImg n'est
        // pas affiché (toujours walk)
        if (isJumping) {
            drawImg(g, playerWalk2Img, x, y, width, height);
        } else if (isFalling || !isWalking || !canGoRight || !canGoLeft) {
            drawImg(g, playerStopImg, x, y, width, height);
        } else if (walkNum <= 10) {
            drawImg(g, playerWalk1Img, x, y, width, height);
        } else if (!isJumping && !isFalling && walkNum <= 20) {
            drawImg(g, playerWalk2Img, x, y, width, height);
        }
    }

    private void drawImg(Graphics g, BufferedImage img, int x, int y, int width, int height) {
        if (isToRight) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            g.drawImage(img, x + width, y, -width, height, null);
        }
    }

    public void setFalling(boolean isFalling) {
        this.isFalling = isFalling;
    }

    public void setWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    public void setToRight(boolean isToRight) {
        this.isToRight = isToRight;
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

}
