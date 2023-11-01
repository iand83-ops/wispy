package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.registry.Materials;
import fr.nicolas.wispy.game.items.registry.Items;

public class ReplaceableBlock extends Block {

	public ReplaceableBlock(Blocks type, Items itemType) {
		super(type, Materials.NON_SOLID_TRANSPARENT, itemType);
	}

	@Override
	public Block copyClass() {
		return new ReplaceableBlock(this.getType(), this.getItemType());
	}

	@Override
	public boolean canReplace() {
		return true;
	}
}
