package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.items.registry.Tools;

public class OreBlock extends Block {

	public OreBlock() {
		tools(Tools.PICKAXE);
	}

	@Override
	public boolean canBreak() {
		return true;
	}

	@Override
	public Block copyClass() {
		return new OreBlock();
	}
}
