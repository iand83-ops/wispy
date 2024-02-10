package fr.nicolas.wispy.game.structure;

import fr.nicolas.wispy.game.blocks.registry.Blocks;

public class StructureDefinition {

    private final char character;
    private final Blocks block;

    public StructureDefinition(char character, Blocks block) {
        this.character = character;
        this.block = block;
    }

    public char getCharacter() {
        return this.character;
    }

    public Blocks getBlock() {
        return this.block;
    }
}
