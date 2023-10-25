package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registery.Blocks;

public class OreBlock extends Block {

	public OreBlock(Blocks type) {
		super(type);
	}

	@Override
	public Block copyClass() {
		return new OreBlock(this.getType());
	}
}
