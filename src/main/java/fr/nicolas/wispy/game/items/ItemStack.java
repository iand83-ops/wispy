package fr.nicolas.wispy.game.items;

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

}
