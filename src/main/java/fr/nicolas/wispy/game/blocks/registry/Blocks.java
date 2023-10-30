package fr.nicolas.wispy.game.blocks.registry;

import fr.nicolas.wispy.game.blocks.Block;

public enum Blocks {

    AIR,
    STONE,
    DIRT,
    GRASS,
    SAND,
    WATER,
    OAK_LOG,
    OAK_LEAVES,
    IRON_ORE,
    COAL_ORE,
    GOLD_ORE,
    DIAMOND_ORE,
    BEDROCK;

    private int id = 0;
    private Block block;

    void setId(int id) {
        this.id = id;
    }

    void setBlock(Block block) {
        this.block = block;
    }

    public int getId() {
        return this.id;
    }

    public Block getBlock() {
        return this.block.copy();
    }

}
