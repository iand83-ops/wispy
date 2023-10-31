package fr.nicolas.wispy.game.craft;

import fr.nicolas.wispy.game.items.ItemStack;

public class Craft {

    private final ItemStack result;
    private final Recipe recipe;
    private final Definition[] definitions;

    public Craft(ItemStack result, Recipe recipe, Definition... definitions) {
        this.result = result;
        this.recipe = recipe;
        this.definitions = definitions;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public Definition[] getDefinitions() {
        return this.definitions;
    }

}
