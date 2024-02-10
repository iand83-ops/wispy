package fr.nicolas.wispy.game.items;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.structure.StructureDefinition;
import fr.nicolas.wispy.game.structure.StructureShape;
import fr.nicolas.wispy.game.world.WorldManager;

import java.util.Arrays;
import java.util.Objects;

public class ItemStructure extends Item {

    private Blocks[][] blockTypes;

    public ItemStructure(Items type, String name, StructureShape shape, StructureDefinition... definitions) {
        super(type, name);
        this.blockTypes = new Blocks[shape.getHeight()][shape.getWidth()];

        for (int y = 0; y < shape.getHeight(); y++) {
            String line = shape.getShape()[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == ' ') {
                    continue;
                }

                for (StructureDefinition definition : definitions) {
                    if (definition.getCharacter() == c) {
                        this.blockTypes[y][x] = definition.getBlock();
                    }
                }
            }
        }
    }

    private ItemStructure(Items type, String name, Blocks[][] blockTypes) {
        super(type, name);
        this.blockTypes = blockTypes;
    }

    @Override
    public void useItem(WorldManager worldManager, ItemStack stack, Block block, int x, int y) {
        Player player = Game.getInstance().getPlayer();

        for (int structureY = 0; structureY < this.blockTypes.length; structureY++) {
            for (int structureX = 0; structureX < this.blockTypes[structureY].length; structureX++) {
                Blocks blockType = this.blockTypes[structureY][structureX];
                if (blockType == null) {
                    continue;
                }

                int blockX = x + structureX;
                int blockY = y - this.blockTypes.length + structureY + 1;

                Block blockAt = worldManager.getBlock(blockX, blockY);
                if (!blockAt.canReplace()) {
                    return;
                }

                AABB blockAABB = new AABB(new Vector2D(blockX, blockY - 1), new Vector2D(blockX + 1, blockY));

                if (blockAABB.intersects(player.getBoundingBox())) {
                    return;
                }
            }
        }

        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() <= 0) {
            player.getInventory().setItem(Game.getInstance().getIngameUI().getSelectedSlot(), null);
        }

        for (int structureY = 0; structureY < this.blockTypes.length; structureY++) {
            for (int structureX = 0; structureX < this.blockTypes[structureY].length; structureX++) {
                Blocks blockType = this.blockTypes[structureY][structureX];
                if (blockType == null) {
                    continue;
                }

                int blockX = x + structureX;
                int blockY = y - this.blockTypes.length + structureY + 1;

                worldManager.setBlock(blockX, blockY, blockType.getBlock());
            }
        }
    }

    @Override
    public Item copyClass() {
        return new ItemStructure(this.getType(), this.getName(), this.blockTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemStructure itemBlock = (ItemStructure) o;
        return Arrays.deepEquals(blockTypes, itemBlock.blockTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.deepHashCode(blockTypes));
    }
}
