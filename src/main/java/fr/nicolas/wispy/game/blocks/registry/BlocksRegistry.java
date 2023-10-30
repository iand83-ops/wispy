package fr.nicolas.wispy.game.blocks.registry;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BreakableBackgroundBlock;
import fr.nicolas.wispy.game.blocks.OreBlock;
import fr.nicolas.wispy.game.blocks.WaterBlock;
import fr.nicolas.wispy.game.items.registry.Items;

public class BlocksRegistry {

    private final Block[] blocks = new Block[256];

    public BlocksRegistry() {
        register(0, new Block(Blocks.AIR, Materials.NON_SOLID, null));

        register(1, new Block(Blocks.STONE, Items.STONE));
        register(2, new Block(Blocks.DIRT, Items.DIRT));
        register(3, new Block(Blocks.GRASS, Items.GRASS));
        register(4, new Block(Blocks.SAND, Materials.SOLID_GRAVITY, Items.SAND));

        register(5, new WaterBlock());

        register(6, new BreakableBackgroundBlock(Blocks.OAK_LOG, Items.OAK_LOG));
        register(7, new BreakableBackgroundBlock(Blocks.OAK_LEAVES, Items.OAK_LEAVES));

        register(8, new OreBlock(Blocks.IRON_ORE, Items.IRON_ORE));
        register(9, new OreBlock(Blocks.COAL_ORE, Items.COAL_ORE));
        register(10, new OreBlock(Blocks.GOLD_ORE, Items.GOLD_ORE));
        register(11, new OreBlock(Blocks.DIAMOND_ORE, Items.DIAMOND_ORE));

        register(12, new Block(Blocks.BEDROCK, Materials.SOLID, Items.BEDROCK));
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
