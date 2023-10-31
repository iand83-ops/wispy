package fr.nicolas.wispy.ui;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.ItemStack;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class IngameUI {

    private int selectedSlot = 0;

    public void render(Game game, Graphics2D graphics, int width, int height) {
        Player player = game.getPlayer();

        int size = 48;
        int y = height - size - 5;
        for (int i = 0; i < 9; i++) {
            int x = (width - 9 * (size + 5) - 5) / 2 + i * (size + 5);

            graphics.setColor(new Color(0, 0, 0, 100));
            graphics.fillRect(x - 1, y - 1, size + 2, size + 2);

            ItemStack itemStack = player.getInventory().getItems()[i];
            if (itemStack == null) {
                continue;
            }

            itemStack.render(graphics, x, y, size);
        }

        int x = (width - 9 * (size + 5) - 5) / 2 + selectedSlot * (size + 5);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(x - 2, y - 2, size + 4, size + 4);
        graphics.setColor(Color.WHITE);
        graphics.drawRect(x - 1, y - 1, size + 2, size + 2);
    }

    public boolean keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key >= KeyEvent.VK_1 && key <= KeyEvent.VK_9) {
            this.selectedSlot = key - KeyEvent.VK_1;
            return true;
        }
        return false;
    }

    public boolean mousePressed(MouseEvent e) {
        int size = 48;
        int x = (Game.getInstance().getGameRenderer().getWidth() - 9 * (size + 5) - 5) / 2;
        int y = Game.getInstance().getGameRenderer().getHeight() - size - 5;

        for (int i = 0; i < 9; i++) {
            if (e.getX() >= x + i * (size + 5) - 2 && e.getX() <= x + i * (size + 5) + size + 2 && e.getY() >= y && e.getY() <= y + size) {
                this.selectedSlot = i;
                return true;
            }
        }
        return false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        this.selectedSlot += e.getWheelRotation();
        if (this.selectedSlot < 0) {
            this.selectedSlot = 8;
        } else if (this.selectedSlot > 8) {
            this.selectedSlot = 0;
        }
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }
}
