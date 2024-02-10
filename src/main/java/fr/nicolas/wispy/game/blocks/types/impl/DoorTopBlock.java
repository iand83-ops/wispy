package fr.nicolas.wispy.game.blocks.types.impl;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.registry.Materials;
import fr.nicolas.wispy.game.blocks.types.PairedTopBlock;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.items.registry.Tools;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DoorTopBlock extends PairedTopBlock {

    protected boolean open = false;

    private final BufferedImage sideTexture = Assets.get("blocks/door_side");

    public DoorTopBlock() {
        super(Blocks.DOOR_BOTTOM, true);
        type(Blocks.DOOR_TOP);
        material(Materials.SOLID);
        tools(Tools.AXE);
        itemType(Items.DOOR);
    }

    @Override
    public void onRightClick(WorldManager worldManager, int x, int y) {
        open = !open;

        Block block = worldManager.getBlock(x, y + 1);
        if (block.getType() == pairedBottomBlock) {
            DoorBottomBlock doorBottomBlock = (DoorBottomBlock) block;
            doorBottomBlock.open = open;
        }
    }

    @Override
    public double getWidth() {
        return open ? 1 : 0.2;
    }

    @Override
    public Materials getMaterial() {
        if (open) {
            return Materials.NON_SOLID;
        } else {
            return Materials.SOLID;
        }
    }

    @Override
    public BufferedImage getTexture() {
        if (!open) {
            return sideTexture;
        }
        return super.getTexture();
    }

    @Override
    public boolean renderDarker() {
        return open;
    }

    @Override
    public Block copyClass() {
        return new DoorTopBlock();
    }

    @Override
    protected void read(ByteBuffer buffer) {
        super.read(buffer);
        open = buffer.get() == 1;
    }

    @Override
    protected void write(DataOutputStream out) throws IOException {
        super.write(out);
        out.writeBoolean(open);
    }
}
