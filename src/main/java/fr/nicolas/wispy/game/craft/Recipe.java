package fr.nicolas.wispy.game.craft;

public class Recipe {

    private final String recipe;

    public Recipe(String... recipe) {
        StringBuilder builder = new StringBuilder();
        for (String line : recipe) {
            builder.append(line);

            for (int i = 0; i < 3 - line.length(); i++) {
                builder.append(" ");
            }
        }

        for (int i = 0; i < 3 - recipe.length; i++) {
            builder.append("   ");
        }

        this.recipe = builder.toString();
    }

    @Override
    public String toString() {
        return this.recipe;
    }
}
