package fr.nicolas.wispy.game.world.chunks;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.types.ReplaceableBlock;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;

public class Chunk {

    private final WorldManager worldManager;
    private Block[] blocks;

    private final int[] landLevels = new int[getWidth()];
    private final int[] fluidLevels = new int[getWidth()];

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

        boolean remove = block.getType() == Blocks.AIR;

        Block currentBlock = this.blocks[x + y * getWidth()];

        if (remove) {
            if (currentBlock.getDecorationType() != Blocks.AIR) {
                block = currentBlock.getDecorationType().getBlock();
                block.setDecorationType(currentBlock.getOriginalType());
                block.setOriginalType(currentBlock.getOriginalType());
                block.setBackgroundBlock(true);
            } else if (currentBlock.getOriginalType() != Blocks.AIR) {
                block = currentBlock.getOriginalType().getBlock();
                block.setDecorationType(currentBlock.getOriginalType());
                block.setOriginalType(currentBlock.getOriginalType());
                block.setBackgroundBlock(true);
            }
        }

        this.blocks[x + y * getWidth()] = block;
        if (!remove && !(currentBlock instanceof ReplaceableBlock)) {
            block.setDecorationType(currentBlock.isLiquid() ? block.getDecorationType() : currentBlock.getType());
            block.setOriginalType(currentBlock.getOriginalType());
        }

        computeLevels(x);
    }

    public void overwriteBlock(int x, int y, Block block) {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            return;
        }

        this.blocks[x + y * getWidth()] = block;

        computeLevels(x);
    }

    private void computeLevels(int x) {
        for (int y = 0; y < getHeight(); y++) {
            Block b = getBlock(x, y);
            if (b.isSolid()) {
                landLevels[x] = y;
                break;
            }
        }

        for (int y = 0; y < getHeight(); y++) {
            Block b = getBlock(x, y);
            if (b.isLiquid()) {
                fluidLevels[x] = y;
                break;
            }
        }
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;

        for (int x = 0; x < getWidth(); x++) {
            computeLevels(x);
        }
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

    public int[] getFluidLevels() {
        return this.fluidLevels;
    }

    public int[] getLandLevels() {
        return this.landLevels;
    }
}
