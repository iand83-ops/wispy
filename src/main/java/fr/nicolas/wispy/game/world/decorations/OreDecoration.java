package fr.nicolas.wispy.game.world.decorations;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.world.chunks.Chunk;

import java.util.Random;

public class OreDecoration extends Decoration {

    @Override
    public int[][] getBlocks(Random random) {
        int[][] blocks = new int[4][4];
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
                if (random.nextDouble() > 0.5) {
                    blocks[x][y] = Blocks.IRON_ORE.getId();
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean testBase(Chunk chunk, int x, int y) {
        return testSpace(chunk, x, y);
    }

    @Override
    public boolean testSpace(Chunk chunk, int x, int y) {
        return chunk.getBlock(x, y).getType() == Blocks.STONE;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public double getChance() {
        return 0.01;
    }

    @Override
    public boolean areBlocksNonCollidable() {
        return false;
    }
}
