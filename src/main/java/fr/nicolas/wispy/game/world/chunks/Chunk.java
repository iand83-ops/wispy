package fr.nicolas.wispy.game.world.chunks;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;

public class Chunk {

    private final WorldManager worldManager;
    private Block[] blocks;

    public Chunk(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public Block getBlock(int x, int y) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return Blocks.AIR.getBlock();
        }

        return this.blocks[x + y * getWidth()];
    }
    
    public void setBlock(int x, int y, Block block) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return;
        }

        this.blocks[x + y * getWidth()] = block;
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public Block[] getBlocks() {
        return this.blocks;
    }

    public int getWidth() {
        return WorldManager.CHUNK_WIDTH;
    }

    public int getHeight() {
        return WorldManager.CHUNK_HEIGHT;
    }

}
