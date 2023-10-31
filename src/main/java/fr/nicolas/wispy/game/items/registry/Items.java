package fr.nicolas.wispy.game.items.registry;

import fr.nicolas.wispy.game.items.Item;

public enum Items {

    BLOCK,
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
