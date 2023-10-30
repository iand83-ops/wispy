package fr.nicolas.wispy.game.items;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.image.BufferedImage;

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

        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() <= 0) {
            Game.getInstance().getPlayer().getInventory().setItem(Game.getInstance().getIngameUI().getSelectedSlot(), null);
        }

        worldManager.setBlock(x, y, this.getBlockType().getBlock());
    }

    public Blocks getBlockType() {
        return this.blockType;
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
    public Item copy() {
        ItemBlock item = (ItemBlock) super.copy();
        item.blockType = this.blockType;
        return item;
    }
}
