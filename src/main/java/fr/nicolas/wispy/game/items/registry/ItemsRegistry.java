package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemBlock;

public class ItemsRegistry {

    private final Item[] items = new Item[256];

    public ItemsRegistry() {
//      0 air

        register(1, new ItemBlock(Items.STONE_BLOCK, "Stone", Blocks.STONE));
        register(2, new ItemBlock(Items.DIRT_BLOCK, "Dirt", Blocks.DIRT));
        register(3, new ItemBlock(Items.GRASS_BLOCK, "Grass", Blocks.GRASS));
        register(4, new ItemBlock(Items.SAND_BLOCK, "Sand", Blocks.SAND));

//      5 water

        register(6, new ItemBlock(Items.OAK_LOG_BLOCK, "Oak log", Blocks.OAK_LOG));
        register(7, new ItemBlock(Items.OAK_LEAVES_BLOCK, "Oak leaves", Blocks.OAK_LEAVES));

        register(8, new ItemBlock(Items.IRON_ORE_BLOCK, "Iron ore", Blocks.IRON_ORE));
        register(9, new ItemBlock(Items.COAL_ORE_BLOCK, "Coal ore", Blocks.COAL_ORE));
        register(10, new ItemBlock(Items.GOLD_ORE_BLOCK, "Gold ore", Blocks.GOLD_ORE));
        register(11, new ItemBlock(Items.DIAMOND_ORE_BLOCK, "Diamond ore", Blocks.DIAMOND_ORE));

        register(12, new ItemBlock(Items.BEDROCK_BLOCK, "Bedrock", Blocks.BEDROCK));

//      13 tall grass
//      14 cave vines
//      15 cave vines plant

        register(101, new Item(Items.IRON_INGOT, "Iron ingot"));
        register(102, new Item(Items.COAL, "Coal"));
        register(103, new Item(Items.GOLD_INGOT, "Gold ingot"));
        register(104, new Item(Items.DIAMOND, "Diamond"));
    }

    public void register(int id, Item item) {
        item.getType().setId(id);
        item.getType().setItem(item);
        items[id] = item;
    }

    public Item getItem(int id) {
        return items[id].copy();
    }

}
