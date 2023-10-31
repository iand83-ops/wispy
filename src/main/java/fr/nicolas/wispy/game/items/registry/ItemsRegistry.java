package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.craft.CraftManager;
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
