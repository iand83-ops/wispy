package fr.nicolas.wispy.game.world.decorations;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.chunks.Chunk;

import java.util.Arrays;
import java.util.Random;

public class CaveVinesDecoration extends Decoration {

    @Override
    public int[][] getBlocks(Random random) {
        int[][] blocks = new int[1][1 + random.nextInt(6)];
        Arrays.fill(blocks[0], Blocks.CAVE_VINES_PLANT.getId());
        blocks[0][blocks[0].length - 1] = Blocks.CAVE_VINES.getId();
        return blocks;
    }

    @Override
    public boolean testBase(Chunk chunk, int x, int y) {
        return testSpace(chunk, x, y) && (chunk.getBlock(x, y - 1).getType() == Blocks.STONE && !chunk.getBlock(x, y - 1).isBackgroundBlock());
    }

    @Override
    public boolean testSpace(Chunk chunk, int x, int y) {
        return (chunk.getBlock(x, y).getType() == Blocks.AIR || chunk.getBlock(x, y).isBackgroundBlock()) &&
                (chunk.getBlock(x, y + 1).getType() == Blocks.AIR || chunk.getBlock(x, y + 1).isBackgroundBlock());
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public double getChance() {
        return 0.35;
    }

    @Override
    public boolean areBackgroundBlocks() {
        return false;
    }

    @Override
    public boolean isGoingDown() {
        return true;
    }
}
