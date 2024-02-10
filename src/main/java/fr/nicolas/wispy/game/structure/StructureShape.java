package fr.nicolas.wispy.game.structure;

public class StructureShape {

    private final String[] shape;
    private int width;

    public StructureShape(String... shape) {
        this.shape = shape;

        for (String line : shape) {
            width = Math.max(width, line.length());
        }

        for (int i = 0; i < shape.length; i++) {
            StringBuilder builder = new StringBuilder(shape[i]);
            for (int j = 0; j < width - shape[i].length(); j++) {
                builder.append(" ");
            }

            shape[i] = builder.toString();
        }
    }

    public String[] getShape() {
        return this.shape;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.shape.length;
    }
}
