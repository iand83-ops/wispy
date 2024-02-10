package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.items.Item;

public enum Items {

    BLOCK,
    IRON_INGOT,
    COAL,
    GOLD_INGOT,
    DIAMOND,
    STICK,
    WOODEN_PICKAXE,
    STONE_PICKAXE,
    IRON_PICKAXE,
    GOLD_PICKAXE,
    DIAMOND_PICKAXE,
    WOODEN_AXE,
    STONE_AXE,
    IRON_AXE,
    GOLD_AXE,
    DIAMOND_AXE,
    WOODEN_SHOVEL,
    STONE_SHOVEL,
    IRON_SHOVEL,
    GOLD_SHOVEL,
    DIAMOND_SHOVEL,
    WOODEN_HOE,
    STONE_HOE,
    IRON_HOE,
    GOLD_HOE,
    DIAMOND_HOE,
    WOODEN_SWORD,
    STONE_SWORD,
    IRON_SWORD,
    GOLD_SWORD,
    DIAMOND_SWORD,
    DOOR;

    private int id = 0;
    private Item item;

    void setId(int id) {
        this.id = id;
    }

    void setItem(Item item) {
        this.item = item;
    }

    public int getId() {
        return this.id;
    }

    public Item getItem() {
        return this.item;
    }
}
