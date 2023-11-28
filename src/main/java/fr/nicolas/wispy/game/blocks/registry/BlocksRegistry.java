package fr.nicolas.wispy.game.blocks.registry;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.types.*;
import fr.nicolas.wispy.game.craft.CraftManager;
import fr.nicolas.wispy.game.craft.Definition;
import fr.nicolas.wispy.game.craft.Recipe;
import fr.nicolas.wispy.game.items.ItemBlock;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.items.registry.ItemsRegistry;
import fr.nicolas.wispy.game.items.registry.Tools;

public class BlocksRegistry {

    private final Block[] blocks = new Block[256];
    private final ItemsRegistry itemsRegistry;

    public BlocksRegistry(ItemsRegistry itemsRegistry) {
        this.itemsRegistry = itemsRegistry;

        register(0, new Block().type(Blocks.AIR).material(Materials.NON_SOLID), "Air");

        register(1, new Block().type(Blocks.STONE).blocksItemType(Blocks.COBBLESTONE).tools(Tools.PICKAXE), "Stone");
        register(2, new Block().type(Blocks.DIRT).blocksItemType(Blocks.DIRT).tools(Tools.SHOVEL).durability(80), "Dirt");
        register(3, new Block().type(Blocks.GRASS).blocksItemType(Blocks.DIRT).tools(Tools.SHOVEL).durability(80), "Grass");
        register(4, new Block().type(Blocks.SAND).material(Materials.SOLID_GRAVITY).blocksItemType(Blocks.SAND).tools(Tools.SHOVEL).durability(80), "Sand");

        register(5, new WaterBlock(), "Water");

        register(6, new BreakableBackgroundBlock().type(Blocks.OAK_LOG).blocksItemType(Blocks.OAK_LOG).tools(Tools.AXE), "Oak log");
        register(7, new BreakableBackgroundBlock().type(Blocks.OAK_LEAVES).durability(30), "Oak leaves");

        register(8, new OreBlock().type(Blocks.IRON_ORE).blocksItemType(Blocks.IRON_ORE), "Iron ore");
        register(9, new OreBlock().type(Blocks.COAL_ORE).itemType(Items.COAL), "Coal ore");
        register(10, new OreBlock().type(Blocks.GOLD_ORE).blocksItemType(Blocks.GOLD_ORE), "Gold ore");
        register(11, new OreBlock().type(Blocks.DIAMOND_ORE).itemType(Items.DIAMOND), "Diamond ore");

        register(12, new Block().type(Blocks.BEDROCK).material(Materials.SOLID).blocksItemType(Blocks.BEDROCK).durability(Integer.MAX_VALUE), "Bedrock");

        register(13, new BoundBottomBlock().type(Blocks.TALL_GRASS).material(Materials.NON_SOLID_TRANSPARENT).durability(15), "Tall grass");
        register(14, new BoundTopBlock().type(Blocks.CAVE_VINES).material(Materials.NON_SOLID_TRANSPARENT).durability(15), "Cave vines");
        register(15, new BoundTopBlock().type(Blocks.CAVE_VINES_PLANT).material(Materials.NON_SOLID_TRANSPARENT).durability(15), "Cave vines plant");

        register(16, new CraftingTableBlock(), "Crafting table")
                .registerRecipe(new Recipe("AA", "AA"), new Definition('A', Blocks.OAK_PLANKS));

        register(17, new Block().type(Blocks.OAK_PLANKS).blocksItemType(Blocks.OAK_PLANKS).tools(Tools.AXE), "Oak planks")
                .registerRecipe(4, new Recipe("A"), new Definition('A', Blocks.OAK_LOG));

        register(18, new Block().type(Blocks.COBBLESTONE).blocksItemType(Blocks.COBBLESTONE).tools(Tools.PICKAXE), "Cobblestone");
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
