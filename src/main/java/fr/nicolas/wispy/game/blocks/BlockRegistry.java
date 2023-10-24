package fr.nicolas.wispy.game.blocks;

public class BlockRegistry {

    private final Block[] blocks = new Block[256];

    public BlockRegistry() {
        register(0, new Block(Blocks.AIR, Materials.NON_SOLID));
        register(1, new Block(Blocks.STONE));
        register(2, new Block(Blocks.DIRT));
        register(3, new Block(Blocks.GRASS));
        register(4, new Block(Blocks.SAND, Materials.SOLID_GRAVITY));

        register(5, new Block(Blocks.WATER, Materials.LIQUID));

        register(6, new Block(Blocks.LOG, Materials.SOLID));
        register(7, new Block(Blocks.LEAVES, Materials.SOLID));
    }

    public void register(int id, Block block) {
        block.getType().setId(id);
        block.getType().setBlock(block);
        blocks[id] = block;
    }

    public Block getBlock(int id) {
        return blocks[id].copy();
    }

}
