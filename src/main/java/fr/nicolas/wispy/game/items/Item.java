package fr.nicolas.wispy.game.items;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Item {

    private final Items type;
    private final String name;
    protected final int maxDurability;
    protected int durability;

    public Item(Items type, String name) {
        this(type, name, 0);
    }

    public Item(Items type, String name, int maxDurability) {
        this.type = type;
        this.name = name;
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }

    public void useItem(WorldManager worldManager, ItemStack stack, Block block, int x, int y) {

    }

    public void onBreak(WorldManager worldManager, ItemStack stack, Block block, int x, int y) {

    }

    public boolean canStack() {
        return true;
    }

    public int getId() {
        return this.type.getId();
    }

    public Items getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxDurability() {
        return this.maxDurability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getDurability() {
        return this.durability;
    }

    public BufferedImage getTexture() {
        return Assets.get("items/" + this.getType().name().toLowerCase());
    }

    public Item copyClass() {
        return new Item(this.type, this.name, this.maxDurability);
    }

    public Item copy() {
        Item item = this.copyClass();
        item.durability = this.durability;
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return type == item.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    public void render(Graphics2D graphics, int x, int y, int size) {
        graphics.drawImage(getTexture(), x, y, size, size, null);
    }
}
