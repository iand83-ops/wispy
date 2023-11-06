package fr.nicolas.wispy.game.items.types;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.items.registry.Tools;
import fr.nicolas.wispy.game.items.registry.ToolsMaterial;
import fr.nicolas.wispy.game.world.WorldManager;

public class ToolItem extends Item {

    private final Tools toolType;
    private final ToolsMaterial material;

    public ToolItem(Items type, String name, Tools toolType, ToolsMaterial material) {
        super(type, name, material.getDurability());
        this.toolType = toolType;
        this.material = material;
    }

    @Override
    public void onBreak(WorldManager worldManager, ItemStack stack, Block block, int x, int y) {
        if (this.getDurability() > 0) {
            this.durability--;
        }

        if (this.getDurability() <= 0) {
            Game.getInstance().getPlayer().getInventory().removeItemStack(stack);
        }
    }

    public Tools getToolType() {
        return this.toolType;
    }

    public ToolsMaterial getMaterial() {
        return this.material;
    }

    @Override
    public boolean canStack() {
        return false;
    }

    @Override
    public Item copyClass() {
        return new ToolItem(this.getType(), this.getName(), this.toolType, this.material);
    }
}
