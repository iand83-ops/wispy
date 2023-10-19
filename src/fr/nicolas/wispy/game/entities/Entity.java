package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

public abstract class Entity {

    protected final WorldManager worldManager;

    protected boolean isFalling = false;
    protected boolean isJumping = false;
    protected boolean isWalking = false;
    protected boolean isFacingRight = true;
    protected boolean canGoLeft = true;
    protected boolean canGoRight = true;
    protected boolean canGoUp = true;

    protected double prevX;
    protected double prevY;
    protected double x;
    protected double y;

    protected final double width;
    protected final double height;

    protected long walkTime = 0;
    protected long jumpTime = 0;
    protected long airTime = 0;

    public Entity(WorldManager worldManager, double width, double height) {
        this.worldManager = worldManager;
        this.width = width;
        this.height = height;
    }

    public void computeCollisions() {
        setFalling(true);
        setCanGoLeft(true);
        setCanGoRight(true);
        setCanGoUp(true);

        for (int i = 0; i < this.worldManager.getChunks().length; i++) {
            computeCollision(this.worldManager.getChunks()[i], this.worldManager.getLeftChunkIndex() + i);
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
        double playerWidth = getWidth();
        double playerHeight = getHeight();

        if (playerX + playerWidth < chunkX || playerX > chunkX + chunkWidth) {
            return;
        }

        int chunkY = 0;
        int chunkHeight = chunk[0].length;

        if (playerY + playerHeight < 0 || playerY > chunkHeight) {
            setFalling(true);
            return;
        }

        double playerChunkX = playerX - chunkX;
        double playerChunkY = playerY - chunkY;

        float offset = 1.0F / GameRenderer.BLOCK_RESOLUTION;

        AABB playerLeftAABB = new AABB(new Vector2D(playerChunkX, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX, playerChunkY - offset));
        AABB playerRightAABB = new AABB(new Vector2D(playerChunkX + playerWidth, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX + playerWidth, playerChunkY - offset));
        AABB playerUpAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY - playerHeight), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY - playerHeight));
        AABB playerDownAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY));

        for (int x = (int) -playerWidth; x <= playerWidth; x++) {
            for (int y = (int) -playerHeight; y <= playerHeight; y++) {
                int blockX = (int) (playerChunkX + x);
                int blockY = (int) (playerChunkY + y);

                if (blockX < 0 || blockX >= chunkWidth || blockY < 0 || blockY >= chunkHeight) {
                    continue;
                }

                int blockID = chunk[blockX][blockY];

                Block block = worldManager.getBlockRegistry().getBlock(chunk[blockX][blockY]);

                double blockWidth = block.getWidth();
                double blockHeight = block.getHeight();

                AABB blockAABB = new AABB(new Vector2D(blockX, blockY), new Vector2D(blockX + blockWidth, blockY + blockHeight));

                if (blockID == Blocks.AIR.getId()) {
                    continue;
                }

                if (isFalling && blockAABB.intersects(playerDownAABB)) {
                    setFalling(false);
                }

                if (canGoUp && blockAABB.intersects(playerUpAABB)) {
                    setCanGoUp(false);
                }

                if (canGoRight && blockAABB.intersects(playerRightAABB)) {
                    setCanGoRight(false);
                }

                if (canGoLeft && blockAABB.intersects(playerLeftAABB)) {
                    setCanGoLeft(false);
                }
            }
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
