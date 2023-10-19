package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

public class Player extends Entity {

    public Player(WorldManager worldManager) {
        super(worldManager, 1.0, 2.0);
        this.setIdleTextures(Assets.get("player/idle"));
        this.setWalkTextures(Assets.get("player/walk_1"), Assets.get("player/walk_2"));
        this.setJumpTextures(Assets.get("player/walk_2"));
    }

}
