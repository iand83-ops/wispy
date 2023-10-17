package fr.nicolas.wispy.game.blocks;

public enum Blocks {

    AIR,
    STONE,
    DIRT,
    GRASS,
    SAND;

    private int id = 0;

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
