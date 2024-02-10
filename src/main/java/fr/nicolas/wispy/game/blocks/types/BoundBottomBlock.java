package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;

public class BoundBottomBlock extends Block {

	@Override
	public void onNeighborBreak(WorldManager worldManager, Block neighbor, int x, int y, int neighborX, int neighborY) {
		if (x == neighborX && y + 1 == neighborY) {
			onBreak(worldManager, x, y);
			worldManager.setBlock(x, y, Blocks.AIR.getBlock());
		}
	}

	@Override
	public Block copyClass() {
		return new BoundBottomBlock();
	}
}
