package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.menu.inventory.CraftingInventoryMenu;

public class CraftingTableBlock extends Block {

	public CraftingTableBlock() {
		super(Blocks.CRAFTING_TABLE, Blocks.CRAFTING_TABLE);
	}

	@Override
	public Block copyClass() {
		return new CraftingTableBlock();
	}

	@Override
	public void onRightClick(WorldManager worldManager, int x, int y) {
		Game.getInstance().openMenu(new CraftingInventoryMenu());
	}
}
