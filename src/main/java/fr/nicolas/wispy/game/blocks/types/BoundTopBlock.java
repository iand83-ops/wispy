package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;

public class BoundTopBlock extends Block {

	@Override
	public void onNeighborBreak(WorldManager worldManager, Block block, int x, int y, int neighborX, int neighborY) {
		if (x == neighborX && y - 1 == neighborY) {
			worldManager.setBlock(x, y, Blocks.AIR.getBlock());
			onBreak(worldManager, x, y);
		}
	}

	@Override
	public Block copyClass() {
		return new BoundTopBlock();
	}
}
