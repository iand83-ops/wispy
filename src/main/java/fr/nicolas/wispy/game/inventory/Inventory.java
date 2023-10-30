package fr.nicolas.wispy.game.inventory;

import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.ItemStack;

public class Inventory {

    private final int size;
    private final ItemStack[] items;

    public Inventory(int size) {
        this.size = size;
        this.items = new ItemStack[size];
    }

    public boolean addItem(Item itemStack) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null) {
                this.items[i] = new ItemStack(itemStack, 1);
                return true;
            } else if (this.items[i].getItem().getId() == itemStack.getId() && this.items[i].getAmount() < ItemStack.MAX_STACK_SIZE) {
                this.items[i].setAmount(this.items[i].getAmount() + 1);
                return true;
            }
        }
        return false;
    }

    public void setItem(int slot, ItemStack itemStack) {
        this.items[slot] = itemStack;
    }

    public ItemStack getItem(int slot) {
        return this.items[slot];
    }

    public int getSize() {
        return this.size;
    }

    public ItemStack[] getItems() {
        return this.items;
    }

}
