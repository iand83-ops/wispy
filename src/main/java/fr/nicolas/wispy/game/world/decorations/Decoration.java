package fr.nicolas.wispy.game.world.decorations;

import fr.nicolas.wispy.game.world.chunks.Chunk;

public abstract class Decoration {

    public abstract int[][] getBlocks();

    public abstract boolean testBase(Chunk chunk, int x, int y);

    public abstract boolean testSpace(Chunk chunk, int x, int y);

    public abstract int getPriority();

    public abstract double getChance();

    public abstract boolean areBlocksNonCollidable();

}
