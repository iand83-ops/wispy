package fr.nicolas.wispy.game.blocks.types;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.world.WorldManager;

public class PairedTopBlock extends BoundBottomBlock {

    protected final Blocks pairedBottomBlock;
    private final boolean dropOnlyIfDirect;

    private boolean drop = true;

    public PairedTopBlock(Blocks pairedBottomBlock, boolean dropOnlyIfDirect) {
        this.pairedBottomBlock = pairedBottomBlock;
        this.dropOnlyIfDirect = dropOnlyIfDirect;
    }

    @Override
    public void onNeighborBreak(WorldManager worldManager, Block neighbor, int x, int y, int neighborX, int neighborY) {
        if (neighbor.getType() != pairedBottomBlock) {
            return;
        }

        drop = !dropOnlyIfDirect;
        super.onNeighborBreak(worldManager, neighbor, x, y, neighborX, neighborY);
        drop = true;
    }

    @Override
    public Items getItemType() {
        if (!drop) {
            return null;
        }
        return super.getItemType();
    }

    @Override
    public Block copyClass() {
        return new PairedTopBlock(pairedBottomBlock, dropOnlyIfDirect);
    }
}
