package fr.nicolas.wispy.game.blocks;

public class BlockRegistry {

    private final Block[] blocks = new Block[256];

    public BlockRegistry() {
        register(0, new Block(Blocks.AIR, 1.0, 1.0));
        register(1, new Block(Blocks.STONE, 1.0, 1.0));
        register(2, new Block(Blocks.DIRT, 1.0, 1.0));
        register(3, new Block(Blocks.GRASS, 1.0, 1.0));
        register(4, new Block(Blocks.SAND, 1.0, 1.0));
    }

    public void register(int id, Block block) {
        block.getType().setId(id);
        blocks[id] = block;
    }

    public Block getBlock(int id) {
        return blocks[id];
    }

}
