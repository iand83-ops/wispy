package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.items.Item;

public enum Items {

    STONE,
    DIRT,
    GRASS,
    SAND,
    OAK_LOG,
    OAK_LEAVES,
    IRON_ORE,
    COAL_ORE,
    GOLD_ORE,
    DIAMOND_ORE,
    BEDROCK;

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
