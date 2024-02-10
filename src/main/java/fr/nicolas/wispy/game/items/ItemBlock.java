package fr.nicolas.wispy.game.items;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class ItemBlock extends Item {

    private Blocks blockType;

    public ItemBlock(Items type, String name, Blocks blockType) {
        super(type, name);
        this.blockType = blockType;
    }

    @Override
    public void useItem(WorldManager worldManager, ItemStack stack, Block block, int x, int y) {
        if (!block.canReplace()) {
            return;
        }

        Player player = Game.getInstance().getPlayer();
        AABB blockAABB = new AABB(new Vector2D(x, y - 1), new Vector2D(x + 1, y));

        if (!blockAABB.intersects(player.getBoundingBox())) {
            stack.setAmount(stack.getAmount() - 1);
            if (stack.getAmount() <= 0) {
                player.getInventory().setItem(Game.getInstance().getIngameUI().getSelectedSlot(), null);
            }

            worldManager.setBlock(x, y, this.getBlockType().getBlock());
        }
    }

    public Blocks getBlockType() {
        return this.blockType;
    }

    @Override
    public int getId() {
        return this.getBlockType().getId();
    }

    @Override
    public BufferedImage getTexture() {
        return Assets.get("blocks/" + this.getBlockType().name().toLowerCase());
    }

    @Override
    public Item copyClass() {
        return new ItemBlock(this.getType(), this.getName(), this.blockType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemBlock itemBlock = (ItemBlock) o;
        return blockType == itemBlock.blockType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), blockType);
    }
}
