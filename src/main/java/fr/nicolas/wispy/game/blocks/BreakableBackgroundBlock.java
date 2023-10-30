package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Items;

public class BreakableBackgroundBlock extends Block {

	public BreakableBackgroundBlock(Blocks type, Items itemType) {
		super(type, itemType);
	}

	@Override
	public Block copyClass() {
		return new BreakableBackgroundBlock(this.getType(), this.getItemType());
	}

	@Override
	public boolean canBreak() {
		return true;
	}

}
