package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;

public class BreakableBackgroundBlock extends Block {

	@Override
	public Block copyClass() {
		return new BreakableBackgroundBlock();
	}

	@Override
	public boolean canBreak() {
		return true;
	}

}
