package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BlockLocation;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.Rendering;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity implements Rendering {

    protected final WorldManager worldManager;

    protected BlockLocation ceilingCollision = null;
    protected BlockLocation groundCollision = null;
    protected BlockLocation leftCollision = null;
    protected BlockLocation rightCollision = null;

    protected BlockLocation persistentLiquidCollision = null;
    protected BlockLocation liquidCollision = null;

    protected boolean isWalking = false;
    protected boolean isSprinting = false;
    protected boolean isJumping = false;
    protected boolean isFacingRight = true;

    protected double prevX;
    protected double prevY;
    protected double x;
    protected double y;

    protected final double width;
    protected final double height;

    protected long walkTime = 0;
    protected long jumpTime = 0;
    protected long airTime = 0;

    private BufferedImage[] idleTextures = new BufferedImage[0];
    private BufferedImage[] walkTextures = new BufferedImage[0];
    private BufferedImage[] jumpTextures = new BufferedImage[0];

    public Entity(WorldManager worldManager, double width, double height) {
        this.worldManager = worldManager;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics2D g) {
        if (isJumping && liquidCollision == null) {
            drawTexture(g, jumpTextures);
        } else if (!isWalking || (liquidCollision == null && (groundCollision == null || rightCollision != null || leftCollision != null))) {
            drawTexture(g, idleTextures);
        } else {
            drawTexture(g, walkTextures);
        }
    }

    private void drawTexture(Graphics2D g, BufferedImage[] img) {
        if (img.length == 0) {
            return;
        }

        int width = (int) this.width;
        int height = (int) this.height;

        double boxWidth = getCollisionWidth();
        double boxHeight = getCollisionHeight();

        double widthOffset = (width - boxWidth) / 2.0;
        double heightOffset = (height - boxHeight) / 2.0;

        int frameTime = isSprinting ? 100 : 160;
        int frameIndex = (int) (((System.currentTimeMillis() - walkTime) % (frameTime * img.length)) / frameTime);

        boolean heightFacing = useHeightForFacing();

        if (isFacingRight) {
            drawImage(g, img[frameIndex], x - widthOffset, y - height + heightOffset, width, height);
        } else {
            drawImage(g, img[frameIndex],
                    x + (heightFacing ? 0 : width) - widthOffset,
                    y - (heightFacing ? 0 : height) + heightOffset,
                    (heightFacing ? width : -width),
                    (heightFacing ? -height : height));
        }
    }

    @Override
    public void drawImage(Graphics2D graphics, BufferedImage image, double x, double y, int width, int height) {
        double rotation = getRotation();

        graphics.rotate(rotation, x + width / 2.0, y + height / 2.0);
        Rendering.super.drawImage(graphics, image, x, y, width, height);
        graphics.rotate(-rotation, x + width / 2.0, y + height / 2.0);
    }

    public double getRotation() {
        return 0;
    }

    public boolean useHeightForFacing() {
        return false;
    }

    public double getMoveSpeed() {
        double moveSpeed = 0.0035;

        if (persistentLiquidCollision != null) {
            moveSpeed *= 0.25;
        }

        return moveSpeed;
    }

    public void tick(double elapsedTime) {
        computeCollisions();

        prevX = x;
        prevY = y;

        double moveSpeed = elapsedTime * getMoveSpeed();

        // Walking
        if (isWalking) {
            if (walkTime == 0) {
                walkTime = System.currentTimeMillis();
            }

            if (isFacingRight && rightCollision == null) {
                x += moveSpeed * (isSprinting ? 2.0 : 1.2);
            } else if (!isFacingRight && leftCollision == null) {
                x -= moveSpeed * (isSprinting ? 2.0 : 1.2);
            }
        } else {
            walkTime = 0;
        }

        validateMovementX();

        // Jump
        if (isJumping && ceilingCollision == null) {
            if (jumpTime == 0) {
                jumpTime = System.currentTimeMillis();
                airTime = 0;
                groundCollision = null;
            } else if (liquidCollision != null) {
                jumpTime = System.currentTimeMillis();
                airTime = 0;
                isJumping = false;
            } else if (groundCollision != null && System.currentTimeMillis() - jumpTime > 100) {
                jumpTime = 0;
                isJumping = false;
            }
        } else {
            jumpTime = 0;
        }

        // Gravity
        if (groundCollision == null) {
            if (airTime == 0) {
                airTime = System.currentTimeMillis();
            }
        } else {
            airTime = 0;
        }

        if (airTime != 0) {
            if (this.liquidCollision == null) {
                y += Math.min(easeIn((System.currentTimeMillis() - airTime) / 500.0), 2.5) * moveSpeed * 5;
            } else {
                y += (0.5 + Math.min((System.currentTimeMillis() - airTime) / 1000.0, 0.5)) * moveSpeed;
            }
        }

        if (jumpTime != 0) {
            if (this.liquidCollision == null) {
                y -= moveSpeed * 3;
            } else {
                y -= moveSpeed * 2;
            }
        }

        validateMovementY();
    }

    public static double easeIn(double t) {
        return t * t;
    }

    public void computeCollisions() {
        setGroundCollision(null);
        setLeftCollision(null);
        setRightCollision(null);
        setCeilingCollision(null);

        BlockLocation liquidCollision = this.persistentLiquidCollision;
        setLiquidCollision(null);

        for (int i = 0; i < this.worldManager.getChunks().length; i++) {
            computeCollision(this.worldManager.getChunks()[i], this.worldManager.getLeftChunkIndex() + i);
        }

        if (this.liquidCollision != null) {
            setPersistentLiquidCollision(this.liquidCollision);
        } else if (this.groundCollision == null && liquidCollision != null) {
            setPersistentLiquidCollision(liquidCollision);
        } else {
            setPersistentLiquidCollision(null);
        }
    }

    private void computeCollision(int[][] chunk, int chunkIndex) {
        if (chunk == null) {
            return;
        }

        int chunkWidth = chunk.length;
        int chunkX = chunkWidth * chunkIndex;

        double playerX = getX();
        double playerY = getY();
        double playerWidth = getCollisionWidth();
        double playerHeight = getCollisionHeight();

        if (playerX + playerWidth < chunkX || playerX > chunkX + chunkWidth) {
            return;
        }

        int chunkY = 0;
        int chunkHeight = chunk[0].length;

        if (playerY + playerHeight < 0 || playerY > chunkHeight) {
            setGroundCollision(null);
            return;
        }

        double playerChunkX = playerX - chunkX;
        double playerChunkY = playerY - chunkY;

        double offset = 1.0 / GameRenderer.BLOCK_RESOLUTION;

        AABB playerLeftAABB = new AABB(new Vector2D(playerChunkX, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX, playerChunkY - offset));
        AABB playerRightAABB = new AABB(new Vector2D(playerChunkX + playerWidth, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX + playerWidth, playerChunkY - offset));
        AABB playerUpAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY - playerHeight), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY - playerHeight));
        AABB playerDownAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY));
        Vector2D playerCenterPoint = new Vector2D(playerChunkX + playerWidth / 2.0, playerChunkY - playerHeight / 2.0);

        for (int x = (int) -playerWidth; x <= playerWidth; x++) {
            for (int y = (int) -playerHeight; y <= playerHeight; y++) {
                int blockX = (int) (playerChunkX + x);
                int blockY = (int) (playerChunkY + y);

                if (blockX < 0 || blockX >= chunkWidth || blockY < 0 || blockY >= chunkHeight) {
                    continue;
                }

                Block block = worldManager.getBlockRegistry().getBlock(chunk[blockX][blockY]);

                double blockWidth = block.getWidth();
                double blockHeight = block.getHeight();

                AABB blockAABB = new AABB(new Vector2D(blockX, blockY), new Vector2D(blockX + blockWidth, blockY + blockHeight));

                BlockLocation location = new BlockLocation(block, chunkX + blockX, chunkY + blockY);

                if (block.isLiquid()) {
                    if (blockAABB.contains(playerCenterPoint)) {
                        setLiquidCollision(location);
                    }
                    continue;
                }

                if (!block.isSolid()) {
                    continue;
                }

                if (groundCollision == null && blockAABB.intersects(playerDownAABB)) {
                    setGroundCollision(location);
                }

                if (ceilingCollision == null && blockAABB.intersects(playerUpAABB)) {
                    setCeilingCollision(location);
                }

                if (rightCollision == null && blockAABB.intersects(playerRightAABB)) {
                    setRightCollision(location);
                }

                if (leftCollision == null && blockAABB.intersects(playerLeftAABB)) {
                    setLeftCollision(location);
                }
            }
        }
    }

    public void validateMovementX() {
        BlockLocation previousRightCollision = null;
        BlockLocation previousLeftCollision = null;
        do {
            computeCollisions();

            if ((rightCollision == null || rightCollision.equals(previousRightCollision)) &&
                    (leftCollision == null || leftCollision.equals(previousLeftCollision))) {
                break;
            }

            if (rightCollision != null) {
                x = rightCollision.getX() - getCollisionWidth();
            } else {
                x = leftCollision.getX() + 1;
            }

            previousRightCollision = rightCollision;
            previousLeftCollision = leftCollision;
        } while (true);
    }

    public void validateMovementY() {
        BlockLocation previousCollisionCeiling = null;
        BlockLocation previousCollisionGround = null;
        do {
            computeCollisions();

            if ((groundCollision == null || groundCollision.equals(previousCollisionGround)) &&
                    (ceilingCollision == null || ceilingCollision.equals(previousCollisionCeiling))) {
                break;
            }

            if (groundCollision != null) {
                y = groundCollision.getY();
            } else {
                y = ceilingCollision.getY() + 1;
            }

            previousCollisionGround = groundCollision;
            previousCollisionCeiling = ceilingCollision;
        } while (true);
    }

    public void setGroundCollision(BlockLocation isFalling) {
        this.groundCollision = isFalling;
    }

    public void setLeftCollision(BlockLocation leftCollision) {
        this.leftCollision = leftCollision;
    }

    public void setRightCollision(BlockLocation rightCollision) {
        this.rightCollision = rightCollision;
    }

    public void setCeilingCollision(BlockLocation ceilingCollision) {
        this.ceilingCollision = ceilingCollision;
    }

    public void setLiquidCollision(BlockLocation liquidCollision) {
        this.liquidCollision = liquidCollision;
    }

    public void setPersistentLiquidCollision(BlockLocation persistentLiquidCollision) {
        this.persistentLiquidCollision = persistentLiquidCollision;
    }

    public void setWalking(boolean isWalking) {
        this.isWalking = isWalking;
    }

    public void setSprinting(boolean sprinting) {
        isSprinting = sprinting;
    }

    public void setFacingRight(boolean isToRight) {
        this.isFacingRight = isToRight;
    }

    public void jump() {
        if (this.groundCollision != null || this.liquidCollision != null) {
            this.isJumping = true;
        }
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

    public double getCollisionWidth() {
        return this.width;
    }

    public double getCollisionHeight() {
        return this.height;
    }

    public void setIdleTextures(BufferedImage... idleTextures) {
        this.idleTextures = idleTextures;
    }

    public void setWalkTextures(BufferedImage... walkTextures) {
        this.walkTextures = walkTextures;
    }

    public void setJumpTextures(BufferedImage... jumpTextures) {
        this.jumpTextures = jumpTextures;
    }
}
