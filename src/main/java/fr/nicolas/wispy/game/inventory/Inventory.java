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

    public boolean addItem(Item item) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] == null) {
                this.items[i] = new ItemStack(item, 1);
                return true;
            } else if (item.canStack() && this.items[i].getItem().getId() == item.getId() && this.items[i].getAmount() < ItemStack.MAX_STACK_SIZE) {
                this.items[i].setAmount(this.items[i].getAmount() + 1);
                return true;
            }
        }
        return false;
    }

    public boolean addItemStack(ItemStack stack) {
        for (int i = 0; i < this.items.length; i++) {
            if (stack == this.items[i]) {
                return false;
            }

            if (this.items[i] == null) {
                this.items[i] = stack;
                return true;
            } else if (stack.getItem().canStack() && this.items[i].getItem().getId() == stack.getItem().getId() && this.items[i].getAmount() < ItemStack.MAX_STACK_SIZE) {
                int amount = this.items[i].getAmount() + stack.getAmount();
                if (amount > ItemStack.MAX_STACK_SIZE) {
                    this.items[i].setAmount(ItemStack.MAX_STACK_SIZE);
                    stack.setAmount(amount - ItemStack.MAX_STACK_SIZE);
                } else {
                    this.items[i].setAmount(amount);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeItemStack(ItemStack itemStack) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i] != null && this.items[i] == itemStack) {
                this.items[i] = null;
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
