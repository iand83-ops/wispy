package fr.nicolas.wispy.game.blocks.registry;

import fr.nicolas.wispy.game.blocks.*;
import fr.nicolas.wispy.game.craft.CraftManager;
import fr.nicolas.wispy.game.craft.Definition;
import fr.nicolas.wispy.game.craft.Recipe;
import fr.nicolas.wispy.game.items.ItemBlock;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.items.registry.ItemsRegistry;

public class BlocksRegistry {

    private final Block[] blocks = new Block[256];
    private final ItemsRegistry itemsRegistry;

    public BlocksRegistry(ItemsRegistry itemsRegistry) {
        this.itemsRegistry = itemsRegistry;

        register(0, new Block(Blocks.AIR, Materials.NON_SOLID, (Items) null), "Air");

        register(1, new Block(Blocks.STONE, Blocks.STONE), "Stone");
        register(2, new Block(Blocks.DIRT, Blocks.DIRT), "Dirt");
        register(3, new Block(Blocks.GRASS, Blocks.DIRT), "Grass");
        register(4, new Block(Blocks.SAND, Materials.SOLID_GRAVITY, Blocks.SAND), "Sand");

        register(5, new WaterBlock(), "Water");

        register(6, new BreakableBackgroundBlock(Blocks.OAK_LOG, Blocks.OAK_LOG), "Oak log");
        register(7, new BreakableBackgroundBlock(Blocks.OAK_LEAVES, Blocks.OAK_LEAVES), "Oak leaves");

        register(8, new OreBlock(Blocks.IRON_ORE, Blocks.IRON_ORE), "Iron ore");
        register(9, new OreBlock(Blocks.COAL_ORE, Items.COAL), "Coal ore");
        register(10, new OreBlock(Blocks.GOLD_ORE, Blocks.GOLD_ORE), "Gold ore");
        register(11, new OreBlock(Blocks.DIAMOND_ORE, Items.DIAMOND), "Diamond ore");

        register(12, new Block(Blocks.BEDROCK, Materials.SOLID, Blocks.BEDROCK), "Bedrock");

        register(13, new ReplaceableBlock(Blocks.TALL_GRASS, null), "Tall grass");
        register(14, new ReplaceableBlock(Blocks.CAVE_VINES, null), "Cave vines");
        register(15, new ReplaceableBlock(Blocks.CAVE_VINES_PLANT, null), "Cave vines plant");

        register(16, new Block(Blocks.CRAFTING_TABLE, Blocks.CRAFTING_TABLE), "Crafting table")
                .registerRecipe(new Recipe("AA", "AA"), new Definition('A', Blocks.OAK_PLANKS));

        register(17, new Block(Blocks.OAK_PLANKS, Blocks.OAK_PLANKS), "Oak planks")
                .registerRecipe(4, new Recipe("A"), new Definition('A', Blocks.OAK_LOG));
    }

    public CraftManager register(int id, Block block, String name) {
        if (blocks[id] != null) {
            throw new IllegalArgumentException("Block id " + id + " is already registered");
        }

        block.getType().setId(id);
        block.getType().setBlock(block);
        blocks[id] = block;

        return itemsRegistry.register(id, new ItemBlock(Items.BLOCK, name, block.getType()));
    }

    public Block getBlock(int id) {
        return blocks[id].copy();
    }

    public Block[] getBlocks() {
        return this.blocks;
    }
}
