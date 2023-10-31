package fr.nicolas.wispy.game.items;

import java.awt.*;

public class ItemStack {

    public static final int MAX_STACK_SIZE = 64;

    private final Item item;
    private int amount;

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public ItemStack(Item item) {
        this(item, 1);
    }

    public Item getItem() {
        return item;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void render(Graphics2D graphics, int x, int y, int size) {
        int offset = size / 4;
        item.render(graphics, x + offset / 2, y + offset / 2, size - offset);

        graphics.setFont(new Font("Arial", Font.BOLD, 20));
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (amount == 1) {
            return;
        }

        int textX = x + size - graphics.getFontMetrics().stringWidth(String.valueOf(amount)) - 2;
        int textY = y + size - 2;

        graphics.setColor(Color.BLACK);
        graphics.drawString(String.valueOf(amount), textX + 1, textY + 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(amount), textX, textY);
    }
}
