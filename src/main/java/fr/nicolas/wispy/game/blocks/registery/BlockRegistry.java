package fr.nicolas.wispy.game.blocks.registery;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.OreBlock;
import fr.nicolas.wispy.game.blocks.WaterBlock;

public class BlockRegistry {

    private final Block[] blocks = new Block[256];

    public BlockRegistry() {
        register(0, new Block(Blocks.AIR, Materials.NON_SOLID));
        register(1, new Block(Blocks.STONE));
        register(2, new Block(Blocks.DIRT));
        register(3, new Block(Blocks.GRASS));
        register(4, new Block(Blocks.SAND, Materials.SOLID_GRAVITY));

        register(5, new WaterBlock());

        register(6, new Block(Blocks.OAK_LOG, Materials.SOLID));
        register(7, new Block(Blocks.OAK_LEAVES, Materials.SOLID));

        register(8, new OreBlock(Blocks.IRON_ORE));
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
