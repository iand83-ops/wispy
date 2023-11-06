package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Tools;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.ui.menu.inventory.CraftingInventoryMenu;

public class CraftingTableBlock extends Block {

	public CraftingTableBlock() {
		type(Blocks.CRAFTING_TABLE);
		blocksItemType(Blocks.CRAFTING_TABLE);
		tools(Tools.AXE);
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
