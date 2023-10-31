package fr.nicolas.wispy.game.craft;

import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.items.registry.Items;

public class Definition {

    private final char character;
    private final Items item;
    private final Blocks itemBlock;

    public Definition(char character, Items item) {
        this.character = character;
        this.item = item;
        this.itemBlock = null;
    }

    public Definition(char character, Blocks itemBlock) {
        this.character = character;
        this.item = null;
        this.itemBlock = itemBlock;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getItemID() {
        if (this.item != null) {
            return this.item.getId();
        } else {
            return this.itemBlock.getId();
        }
    }

}
