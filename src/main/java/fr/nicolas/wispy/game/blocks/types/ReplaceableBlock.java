package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Materials;

public class ReplaceableBlock extends Block {

	public ReplaceableBlock() {
		material(Materials.NON_SOLID_TRANSPARENT);
	}

	@Override
	public Block copyClass() {
		return new ReplaceableBlock();
	}

	@Override
	public boolean canReplace() {
		return true;
	}
}
