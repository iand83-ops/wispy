package fr.nicolas.wispy.game.blocks.registry;

import fr.nicolas.wispy.game.blocks.*;
import fr.nicolas.wispy.game.items.registry.Items;

public class BlocksRegistry {

    private final Block[] blocks = new Block[256];

    public BlocksRegistry() {
        register(0, new Block(Blocks.AIR, Materials.NON_SOLID, null));

        register(1, new Block(Blocks.STONE, Items.STONE_BLOCK));
        register(2, new Block(Blocks.DIRT, Items.DIRT_BLOCK));
        register(3, new Block(Blocks.GRASS, Items.DIRT_BLOCK));
        register(4, new Block(Blocks.SAND, Materials.SOLID_GRAVITY, Items.SAND_BLOCK));

        register(5, new WaterBlock());

        register(6, new BreakableBackgroundBlock(Blocks.OAK_LOG, Items.OAK_LOG_BLOCK));
        register(7, new BreakableBackgroundBlock(Blocks.OAK_LEAVES, Items.OAK_LEAVES_BLOCK));

        register(8, new OreBlock(Blocks.IRON_ORE, Items.IRON_ORE_BLOCK));
        register(9, new OreBlock(Blocks.COAL_ORE, Items.COAL));
        register(10, new OreBlock(Blocks.GOLD_ORE, Items.GOLD_ORE_BLOCK));
        register(11, new OreBlock(Blocks.DIAMOND_ORE, Items.DIAMOND));

        register(12, new Block(Blocks.BEDROCK, Materials.SOLID, Items.BEDROCK_BLOCK));

        register(13, new ReplaceableBlock(Blocks.TALL_GRASS, null));
        register(14, new ReplaceableBlock(Blocks.CAVE_VINES, null));
        register(15, new ReplaceableBlock(Blocks.CAVE_VINES_PLANT, null));
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
