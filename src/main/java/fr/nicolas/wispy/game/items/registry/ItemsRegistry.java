package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemBlock;

public class ItemsRegistry {

    private final Item[] items = new Item[256];

    public ItemsRegistry() {
//      0 air

        register(1, new ItemBlock(Items.STONE, "Stone", Blocks.STONE));
        register(2, new ItemBlock(Items.DIRT, "Dirt", Blocks.DIRT));
        register(3, new ItemBlock(Items.GRASS, "Grass", Blocks.DIRT));
        register(4, new ItemBlock(Items.SAND, "Sand", Blocks.SAND));

//      5 water

        register(6, new ItemBlock(Items.OAK_LOG, "Oak log", Blocks.OAK_LOG));
        register(7, new ItemBlock(Items.OAK_LEAVES, "Oak leaves", Blocks.OAK_LEAVES));

        register(8, new ItemBlock(Items.IRON_ORE, "Iron ore", Blocks.IRON_ORE));
        register(9, new ItemBlock(Items.COAL_ORE, "Coal ore", Blocks.COAL_ORE));
        register(10, new ItemBlock(Items.GOLD_ORE, "Gold ore", Blocks.GOLD_ORE));
        register(11, new ItemBlock(Items.DIAMOND_ORE, "Diamond ore", Blocks.DIAMOND_ORE));

        register(12, new ItemBlock(Items.BEDROCK, "Bedrock", Blocks.BEDROCK));
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
