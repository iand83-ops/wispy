package fr.nicolas.wispy.game.craft;

import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class CraftManager {

    private final HashMap<RecipeKey, ItemStack> recipes = new HashMap<>();
    private Item currentItem;
    private final ArrayList<Craft> crafts = new ArrayList<>();

    public void loadRecipes() {
        for (Craft craft : crafts) {
            int[] items = new int[9];

            String recipeString = craft.getRecipe().toString();

            for (int i = 0; i < recipeString.length(); i++) {
                char character = recipeString.charAt(i);
                for (Definition definition : craft.getDefinitions()) {
                    if (definition.getCharacter() == character) {
                        items[i] = definition.getItemID();
                    }
                }
            }

            recipes.put(new RecipeKey(items), craft.getResult());
        }

        crafts.clear();
    }

    public void registerRecipe(Recipe recipe, Definition... definitions) {
        registerRecipe(1, recipe, definitions);
    }

    public void registerRecipe(int amount, Recipe recipe, Definition... definitions) {
        crafts.add(new Craft(new ItemStack(currentItem, amount), recipe, definitions));
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
    }

    public ItemStack getResult(ItemStack[] items) {
        if (items.length == 4) {
            ItemStack[] newItems = new ItemStack[9];
            System.arraycopy(items, 0, newItems, 0, 2);
            System.arraycopy(items, 2, newItems, 3, 2);
            items = newItems;
        }

        int[] ids = new int[9];

        int firstLine = -1;
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                ItemStack item = items[i * 3 + x];
                if (item != null) {
                    firstLine = i;
                    break;
                }
            }

            if (firstLine != -1) {
                break;
            }
        }

        int firstColumn = -1;
        for (int i = 0; i < 3; i++) {
            for (int y = 0; y < 3; y++) {
                ItemStack item = items[y * 3 + i];
                if (item != null) {
                    firstColumn = i;
                    break;
                }
            }

            if (firstColumn != -1) {
                break;
            }
        }

        if (firstLine == -1 || firstColumn == -1) {
            return null;
        }

        for (int i = firstLine * 3; i < Math.min(items.length, 9); i++) {
            if (i + firstColumn >= items.length) {
                break;
            }

            ItemStack item = items[i + firstColumn];
            if (item != null) {
                ids[i - firstLine * 3] = item.getItem().getId();
            }
        }

        ItemStack result = recipes.get(new RecipeKey(ids));
        if (result == null) {
            return null;
        }

        return new ItemStack(result.getItem().copy(), result.getAmount());
    }
}
