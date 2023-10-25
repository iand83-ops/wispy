package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registery.Blocks;
import fr.nicolas.wispy.game.blocks.registery.Materials;

public class WaterBlock extends Block {

	public WaterBlock() {
		super(Blocks.WATER, Materials.LIQUID);
	}

	@Override
	public boolean renderAsSolidColor() {
		return true;
	}

	@Override
	public int getSolidColor() {
		return 0x33BACC;
	}

	@Override
	public Block copyClass() {
		return new WaterBlock();
	}
}
