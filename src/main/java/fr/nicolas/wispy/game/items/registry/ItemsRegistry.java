package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.craft.CraftManager;
import fr.nicolas.wispy.game.craft.Definition;
import fr.nicolas.wispy.game.craft.Recipe;
import fr.nicolas.wispy.game.items.Item;

public class ItemsRegistry {

    private final Item[] items = new Item[256];
    private final CraftManager craftManager;

    public ItemsRegistry(CraftManager craftManager) {
        this.craftManager = craftManager;

        register(101, new Item(Items.IRON_INGOT, "Iron ingot"));
        register(102, new Item(Items.COAL, "Coal"));
        register(103, new Item(Items.GOLD_INGOT, "Gold ingot"));
        register(104, new Item(Items.DIAMOND, "Diamond"));

        register(105, new Item(Items.STICK, "Stick"))
                .registerRecipe(4, new Recipe("A", "A"), new Definition('A', Blocks.OAK_PLANKS));

        register(106, new Item(Items.WOODEN_PICKAXE, "Wooden pickaxe"))
                .registerRecipe(new Recipe("AAA", " B ", " B "), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));

        register(107, new Item(Items.WOODEN_AXE, "Wooden axe"))
                .registerRecipe(new Recipe("AA", "AB", " B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));

        register(108, new Item(Items.WOODEN_SHOVEL, "Wooden shovel"))
                .registerRecipe(new Recipe("A", "B", "B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));

        register(109, new Item(Items.WOODEN_HOE, "Wooden hoe"))
                .registerRecipe(new Recipe("AA", " B", " B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));

        register(110, new Item(Items.WOODEN_SWORD, "Wooden sword"))
                .registerRecipe(new Recipe("A", "A", "B"), new Definition('A', Blocks.OAK_PLANKS), new Definition('B', Items.STICK));
    }

    public CraftManager register(int id, Item item) {
        if (items[id] != null) {
            throw new IllegalArgumentException("Item id " + id + " is already registered");
        }

        item.getType().setId(id);
        item.getType().setItem(item);
        items[id] = item;

        craftManager.setCurrentItem(item);
        return craftManager;
    }

    public Item getItem(int id) {
        return items[id].copy();
    }

}
