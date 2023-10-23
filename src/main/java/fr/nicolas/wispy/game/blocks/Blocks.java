package fr.nicolas.wispy.game.blocks;

public enum Blocks {

    AIR,
    STONE,
    DIRT,
    GRASS,
    SAND,
    WATER;

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
        return this.block;
    }
}
