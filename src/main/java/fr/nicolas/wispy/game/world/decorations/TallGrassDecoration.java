package fr.nicolas.wispy.game.world.decorations;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.chunks.Chunk;

import java.util.Random;

public class TallGrassDecoration extends Decoration {

    @Override
    public int[][] getBlocks(Random random) {
        int[][] blocks = new int[1][1];
        blocks[0][0] = Blocks.TALL_GRASS.getId();
        return blocks;
    }

    @Override
    public boolean testBase(Chunk chunk, int x, int y) {
        return testSpace(chunk, x, y) && chunk.getBlock(x, y + 1).getType() == Blocks.GRASS;
    }

    @Override
    public boolean testSpace(Chunk chunk, int x, int y) {
        return chunk.getBlock(x, y).getType() == Blocks.AIR;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public double getChance() {
        return 0.6;
    }

    @Override
    public boolean areBackgroundBlocks() {
        return false;
    }
}
