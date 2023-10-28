package fr.nicolas.wispy.game.blocks.registery;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BreakableBackgroundBlock;
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

        register(6, new BreakableBackgroundBlock(Blocks.OAK_LOG));
        register(7, new BreakableBackgroundBlock(Blocks.OAK_LEAVES));

        register(8, new OreBlock(Blocks.IRON_ORE));
        register(9, new OreBlock(Blocks.COAL_ORE));
        register(10, new OreBlock(Blocks.GOLD_ORE));
        register(11, new OreBlock(Blocks.DIAMOND_ORE));

        register(12, new Block(Blocks.BEDROCK, Materials.SOLID));
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
