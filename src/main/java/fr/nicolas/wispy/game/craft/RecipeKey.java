package fr.nicolas.wispy.game.craft;

import java.util.Arrays;

public class RecipeKey {
    private final int[] array;

    public RecipeKey(int[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        this.array = array;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RecipeKey) {
            RecipeKey other = (RecipeKey) obj;
            return Arrays.equals(array, other.array);
        }
        return false;
    }

    public int[] getArray() {
        return array;
    }

}
