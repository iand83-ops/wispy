package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registery.Blocks;

public class BreakableBackgroundBlock extends Block {

	public BreakableBackgroundBlock(Blocks type) {
		super(type);
	}

	@Override
	public Block copyClass() {
		return new BreakableBackgroundBlock(this.getType());
	}

	@Override
	public boolean canBreak() {
		return true;
	}
}
