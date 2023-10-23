package fr.nicolas.wispy.game.world.chunks;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;

public class Chunk {

    private final WorldManager worldManager;
    private int[][] blocks;
    private int width;
    private int height;

    public Chunk(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public Block getBlock(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Blocks.AIR.getBlock();
        }

        return this.worldManager.getBlockRegistry().getBlock(this.blocks[x][y]);
    }

    public void setBlocks(int[][] blocks) {
        this.blocks = blocks;
        this.width = blocks.length;
        this.height = blocks[0].length;
    }

    public int[][] getBlocks() {
        return this.blocks;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
