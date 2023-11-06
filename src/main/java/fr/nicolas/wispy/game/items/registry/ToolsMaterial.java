package fr.nicolas.wispy.game.items.registry;

public enum ToolsMaterial {

    WOOD(60, 1),
    STONE(132, 2),
    IRON(251, 3),
    GOLD(33, 5),
    DIAMOND(1562, 4);

    final int durability;
    final int efficiency;

    ToolsMaterial(int durability, int efficiency) {
        this.durability = durability;
        this.efficiency = efficiency;
    }

    public int getDurability() {
        return this.durability;
    }

    public int getEfficiency() {
        return this.efficiency;
    }

}
