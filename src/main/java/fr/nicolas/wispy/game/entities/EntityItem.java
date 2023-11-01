package fr.nicolas.wispy.game.entities;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.registry.Entities;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.utils.MathUtils;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EntityItem extends Entity {

    private Item item;
    private final long spawnTick;

    public EntityItem(WorldManager worldManager, Item item) {
        super(Entities.ITEM, worldManager, 0.5, 0.5);
        this.item = item;
        this.spawnTick = Game.getInstance().getGameTick();
        setIdleTextures(item.getTexture());
        setJumpTextures(item.getTexture());
        setWalkTextures(item.getTexture());
    }

    @Override
    public void tick(double elapsedTime) {
        if (liquidCollision != null) {
            isJumping = true;
        }

        super.tick(elapsedTime);

        if (Game.getInstance().getGameTick() - spawnTick > 20 * 60 * 4) {
            this.kill();
        }

        if (Game.getInstance().getGameTick() - spawnTick > 5 && MathUtils.distance(this, Game.getInstance().getPlayer()) < 1) {
            if (Game.getInstance().getPlayer().getInventory().addItem(item.copy())) {
                this.kill();
            }
        }
    }

    @Override
    public void drawImage(Graphics2D graphics, BufferedImage image, double x, double y, double width, double height) {
        double translateY = 0;
        if (this.groundCollision != null) {
            translateY = (Math.sin(System.currentTimeMillis() / 500.0) + 1) / 8.0;
        }

        graphics.translate(0, -translateY);
        super.drawImage(graphics, image, x, y, width, height);
        graphics.translate(0, translateY);
    }

    @Override
    protected void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeInt(item.getId());
    }

    @Override
    protected void read(ByteBuffer buffer) {
        super.read(buffer);
        int i = buffer.getInt();
        item = worldManager.getItemRegistry().getItem(i);
        setIdleTextures(item.getTexture());
        setJumpTextures(item.getTexture());
        setWalkTextures(item.getTexture());
    }

    @Override
    public Entity copyClass() {
        return new EntityItem(worldManager, item);
    }
}
