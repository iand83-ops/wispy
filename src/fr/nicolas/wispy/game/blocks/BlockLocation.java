package fr.nicolas.wispy.game.blocks;

import java.util.Objects;

public class BlockLocation {

    private final Block block;
    private final int x;
    private final int y;

    public BlockLocation(Block block, int x, int y) {
        this.block = block;
        this.x = x;
        this.y = y;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockLocation location = (BlockLocation) o;
        return x == location.x && y == location.y && Objects.equals(block, location.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, x, y);
    }
}
