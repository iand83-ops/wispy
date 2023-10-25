package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registery.BlockLocation;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.utils.MathUtils;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.game.world.chunks.Chunk;
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

    private AABB boundingBox = new AABB(new Vector2D(0, 0), new Vector2D(0, 0));

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

    protected double rotation;

    protected long walkTime = 0;
    protected long jumpTime = 0;
    protected long airTime = 0;
    protected long liquidTime = 0;
    protected long outOfLiquidTime = 0;

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
        return this.rotation;
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
            isJumping = false;
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
                y += Math.min(MathUtils.easeIn((System.currentTimeMillis() - airTime) / 500.0), 2.5) * moveSpeed * 5;
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

    public void computeCollisions() {
        double playerWidth = getCollisionWidth();
        double playerHeight = getCollisionHeight();

        setGroundCollision(null);
        setLeftCollision(null);
        setRightCollision(null);
        setCeilingCollision(null);

        BlockLocation liquidCollision = this.persistentLiquidCollision;
        setLiquidCollision(null);

        for (int i = 0; i < this.worldManager.getChunks().length; i++) {
            computeCollision(this.worldManager.getChunks()[i], this.worldManager.getLeftChunkIndex() + i, playerWidth, playerHeight);
        }

        if (this.liquidCollision != null) {
            if (this.liquidTime == 0) {
                this.liquidTime = System.currentTimeMillis();
            }
        } else {
            this.liquidTime = 0;
        }

        if (this.persistentLiquidCollision != null && this.liquidCollision == null) {
            if (this.outOfLiquidTime == 0) {
                this.outOfLiquidTime = System.currentTimeMillis();
            }
        } else {
            this.outOfLiquidTime = 0;
        }

        if (this.liquidCollision != null) {
            setPersistentLiquidCollision(this.liquidCollision);
        } else if (this.groundCollision == null && liquidCollision != null) {
            setPersistentLiquidCollision(liquidCollision);
        } else {
            setPersistentLiquidCollision(null);
        }
    }

    private void computeCollision(Chunk chunk, int chunkIndex, double playerWidth, double playerHeight) {
        if (chunk == null || chunk.getBlocks() == null) {
            return;
        }

        int chunkWidth = chunk.getWidth();
        int chunkX = chunkWidth * chunkIndex;

        double playerX = getX();
        double playerY = getY();

        if (playerX + playerWidth < chunkX || playerX > chunkX + chunkWidth) {
            return;
        }

        int chunkY = 0;
        int chunkHeight = chunk.getHeight();

        if (playerY + playerHeight < 0 || playerY > chunkHeight) {
            setGroundCollision(null);
            return;
        }

        double playerChunkX = playerX - chunkX;
        double playerChunkY = playerY - chunkY;

        double offset = 1.0 / GameRenderer.BLOCK_RESOLUTION;
        double rotation = getRotation() - (useHeightForFacing() ? Math.toRadians(90) : 0);

        Vector2D playerCenterPoint = new Vector2D(playerChunkX + playerWidth / 2.0, playerChunkY - playerHeight / 2.0);

        Vector2D topLeft = new Vector2D(playerChunkX, playerChunkY - playerHeight);
        Vector2D topRight = new Vector2D(playerChunkX + playerWidth, playerChunkY - playerHeight);
        Vector2D bottomLeft = new Vector2D(playerChunkX, playerChunkY);
        Vector2D bottomRight = new Vector2D(playerChunkX + playerWidth, playerChunkY);

        Vector2D rotatedTopLeft = MathUtils.rotatePoint(topLeft, playerCenterPoint, rotation);
        Vector2D rotatedTopRight = MathUtils.rotatePoint(topRight, playerCenterPoint, rotation);
        Vector2D rotatedBottomLeft = MathUtils.rotatePoint(bottomLeft, playerCenterPoint, rotation);
        Vector2D rotatedBottomRight = MathUtils.rotatePoint(bottomRight, playerCenterPoint, rotation);

        double minX = Math.min(Math.min(rotatedTopLeft.x, rotatedTopRight.x), Math.min(rotatedBottomLeft.x, rotatedBottomRight.x));
        double maxX = Math.max(Math.max(rotatedTopLeft.x, rotatedTopRight.x), Math.max(rotatedBottomLeft.x, rotatedBottomRight.x));
        double minY = Math.min(Math.min(rotatedTopLeft.y, rotatedTopRight.y), Math.min(rotatedBottomLeft.y, rotatedBottomRight.y));
        double maxY = Math.max(Math.max(rotatedTopLeft.y, rotatedTopRight.y), Math.max(rotatedBottomLeft.y, rotatedBottomRight.y));

        this.boundingBox = new AABB(new Vector2D(minX, minY), new Vector2D(maxX, maxY));

        AABB playerLeftAABB = new AABB(new Vector2D(minX, minY + offset), new Vector2D(minX, maxY - offset));
        AABB playerRightAABB = new AABB(new Vector2D(maxX, minY + offset), new Vector2D(maxX, maxY - offset));
        AABB playerUpAABB = new AABB(new Vector2D(minX + offset, minY), new Vector2D(maxX - offset, minY));
        AABB playerDownAABB = new AABB(new Vector2D(minX + offset, maxY), new Vector2D(maxX - offset, maxY));

        for (int x = (int) -playerWidth; x <= playerWidth; x++) {
            for (int y = (int) -playerHeight; y <= playerHeight; y++) {
                int blockX = (int) (playerChunkX + x);
                int blockY = (int) (playerChunkY + y);

                if (blockX < 0 || blockX >= chunkWidth || blockY < 0 || blockY >= chunkHeight) {
                    continue;
                }

                Block block = chunk.getBlock(blockX, blockY);

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

                if (!block.isSolid() || block.isBackgroundBlock()) {
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
        for (int i = 0; i < 20; i++) {
            computeCollisions();

            if ((rightCollision == null || rightCollision.equals(previousRightCollision)) &&
                    (leftCollision == null || leftCollision.equals(previousLeftCollision))) {
                break;
            }

            double boundingBoxWidth = boundingBox.getMax().x - boundingBox.getMin().x;

            if (rightCollision != null) {
                x = Math.min(x, rightCollision.getX() - (boundingBoxWidth - 1.0 / GameRenderer.BLOCK_RESOLUTION));
            } else {
                x = Math.max(x, leftCollision.getX() + leftCollision.getBlock().getHeight() + (boundingBoxWidth - getCollisionWidth()));
            }

            previousRightCollision = rightCollision;
            previousLeftCollision = leftCollision;
        }
    }

    public void validateMovementY() {
        BlockLocation previousCollisionCeiling = null;
        BlockLocation previousCollisionGround = null;
        for (int i = 0; i < 20; i++) {
            computeCollisions();

            if ((groundCollision == null || groundCollision.equals(previousCollisionGround)) &&
                    (ceilingCollision == null || ceilingCollision.equals(previousCollisionCeiling))) {
                break;
            }

            double boundingBoxHeight = boundingBox.getMax().y - boundingBox.getMin().y;

            if (groundCollision != null) {
                y = Math.min(y, groundCollision.getY());
            } else {
                y = Math.max(y, ceilingCollision.getY() + ceilingCollision.getBlock().getHeight() + boundingBoxHeight - (1.0 / GameRenderer.BLOCK_RESOLUTION / 2));
            }

            previousCollisionGround = groundCollision;
            previousCollisionCeiling = ceilingCollision;
        }
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

    public AABB getBoundingBox() {
        return this.boundingBox;
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

    public boolean canGroundJump() {
        return this.groundCollision != null && this.persistentLiquidCollision == null;
    }

    public void jump() {
        if (canGroundJump() ||
                (this.liquidCollision != null && System.currentTimeMillis() - this.liquidTime > 300) ||
                (this.persistentLiquidCollision != null && System.currentTimeMillis() - this.outOfLiquidTime < 100)) {
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
