package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.blocks.registery.Blocks;
import fr.nicolas.wispy.game.blocks.registery.Materials;
import fr.nicolas.wispy.game.world.WorldManager;

public class WaterBlock extends Block {

	public WaterBlock() {
		super(Blocks.WATER, Materials.LIQUID);
	}

	@Override
	public void tick(WorldManager worldManager, int x, int y, long gameTick) {
		if (gameTick % 4 != 0) {
			return;
		}

		spreadVertical(worldManager, x, y + 1);

		Block belowSelf = worldManager.getBlock(x, y + 1);
		spreadHorizontal(worldManager, x + 1, y, belowSelf);
		spreadHorizontal(worldManager, x - 1, y, belowSelf);
	}

	private void spreadVertical(WorldManager worldManager, int x, int y) {
		Block block = worldManager.getBlock(x, y);
		if (block.getType() == Blocks.AIR || block.isBackgroundBlock()) {
			worldManager.setBlock(x, y, Blocks.WATER.getBlock());
		}
	}

	private void spreadHorizontal(WorldManager worldManager, int x, int y, Block belowSelf) {
		Block block = worldManager.getBlock(x, y);
		Block below = worldManager.getBlock(x, y + 1);
		if ((block.getType() == Blocks.AIR || block.isBackgroundBlock()) && ((below.getType() != Blocks.AIR && !below.isBackgroundBlock()) || belowSelf.isSolid())) {
			worldManager.setBlock(x, y, Blocks.WATER.getBlock());
		}
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
	public boolean canBreak() {
		return false;
	}

	@Override
	public Block copyClass() {
		return new WaterBlock();
	}
}
