package fr.nicolas.wispy.ui.menu.inventory;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.utils.Assets;

import java.awt.*;
import java.awt.event.MouseEvent;

public class CraftingInventoryMenu extends InventoryMenu {


    public CraftingInventoryMenu() {
        super(9, 1, Assets.get("menu/crafting_table"));
    }

    @Override
    public void drawBeforeSlots(Graphics2D graphics, int x, int y, double factor) {

    }

    @Override
    public void drawTopSlots(Graphics2D graphics, int x, int y, double factor) {
        drawSlots(graphics, (int) Math.round(x + 30 * factor), (int) Math.round(y + 17 * factor), factor, 36, 3);
        drawSlots(graphics, (int) Math.round(x + 30 * factor), (int) Math.round(y + 35 * factor), factor, 39, 3);
        drawSlots(graphics, (int) Math.round(x + 30 * factor), (int) Math.round(y + 53 * factor), factor, 42, 3);

        drawSlots(graphics, (int) Math.round(x + 124 * factor), (int) Math.round(y + 35 * factor), factor, 45, 1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        this.immutableSlots[0] = Game.getInstance().getWorldManager().getCraftManager().getResult(this.topSlots);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        this.immutableSlots[0] = Game.getInstance().getWorldManager().getCraftManager().getResult(this.topSlots);
    }

    @Override
    public void onSetImmutable(int index, ItemStack itemStack) {
        if (index == 0 && itemStack == null) {
            for (int i = 0; i < this.topSlots.length; i++) {
                if (this.topSlots[i] != null) {
                    this.topSlots[i].setAmount(this.topSlots[i].getAmount() - 1);
                    if (this.topSlots[i].getAmount() <= 0) {
                        this.topSlots[i] = null;
                    }
                }
            }

            this.immutableSlots[0] = Game.getInstance().getWorldManager().getCraftManager().getResult(this.topSlots);
        }
    }
}
