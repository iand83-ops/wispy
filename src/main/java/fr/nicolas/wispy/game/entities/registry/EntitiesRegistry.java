package fr.nicolas.wispy.game.entities.registry;

import fr.nicolas.wispy.game.entities.Entity;
import fr.nicolas.wispy.game.entities.EntityItem;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.world.WorldManager;

public class EntitiesRegistry {

    private final Entity[] entities = new Entity[256];

    public EntitiesRegistry(WorldManager worldManager) {
        register(0, new Player(worldManager));
        register(1, new EntityItem(worldManager, Items.BLOCK.getItem()));
    }

    public void register(int id, Entity entity) {
        entity.getType().setId(id);
        entity.getType().setEntity(entity);
        entities[id] = entity;
    }

    public Entity getEntity(int id) {
        return entities[id].copy();
    }

}
