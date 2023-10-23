package fr.nicolas.wispy.game.world;

public enum Dimensions {

    OVERWORLD(0),
    NETHER(1),
    END(2);

    private int id;

    Dimensions(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
