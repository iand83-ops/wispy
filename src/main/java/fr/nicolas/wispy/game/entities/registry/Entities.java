package fr.nicolas.wispy.game.entities.registry;

import fr.nicolas.wispy.game.entities.Entity;

public enum Entities {

    PLAYER,
    ITEM;

    private int id = 0;
    private Entity entity;

    void setId(int id) {
        this.id = id;
    }

    void setEntity(Entity entity) {
        this.entity = entity;
    }

    public int getId() {
        return this.id;
    }

    public Entity getBlock() {
        return this.entity.copy();
    }

}
