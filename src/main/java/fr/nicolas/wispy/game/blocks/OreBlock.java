package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Items;

public class OreBlock extends Block {

	public OreBlock(Blocks type, Items itemType) {
		super(type, itemType);
	}

	public OreBlock(Blocks type, Blocks blockItemType) {
		super(type, blockItemType);
	}

	@Override
	public boolean canBreak() {
		return true;
	}

	@Override
	public Block copyClass() {
		return new OreBlock(this.getType(), this.getItemType());
	}
}
