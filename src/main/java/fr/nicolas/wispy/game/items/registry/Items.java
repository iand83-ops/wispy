package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.items.Item;

public enum Items {

    STONE_BLOCK,
    DIRT_BLOCK,
    GRASS_BLOCK,
    SAND_BLOCK,
    OAK_LOG_BLOCK,
    OAK_LEAVES_BLOCK,
    IRON_ORE_BLOCK,
    COAL_ORE_BLOCK,
    GOLD_ORE_BLOCK,
    DIAMOND_ORE_BLOCK,
    BEDROCK_BLOCK,
    IRON_INGOT,
    COAL,
    GOLD_INGOT,
    DIAMOND;

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
