package fr.nicolas.wispy.ui.menu;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InventoryMenu extends Menu {

	private final BufferedImage inventoryTexture = Assets.get("menu/inventory");
	private final AABB[] slots;

	private ItemStack itemStack;
	private int offsetX;
	private int offsetY;

	public InventoryMenu() {
		slots = new AABB[Game.getInstance().getPlayer().getInventory().getSize()];
	}

	@Override
	public void close() {
		if (itemStack != null) {
			Game.getInstance().getPlayer().getInventory().addItemStack(itemStack);
		}
	}

	@Override
	public void render(Graphics2D graphics, int width, int height) {
		int inventoryWidth = (int) ((176 / 166.0) * height / 2.0);
		int inventoryHeight = height / 2;

		double factor = inventoryWidth / 176.0;

		int x = width / 2 - inventoryWidth / 2;
		int y = height / 2 - inventoryHeight / 2;

		graphics.drawImage(this.inventoryTexture, x, y, inventoryWidth, inventoryHeight, null);

		drawPlayer(graphics, factor, x, y);

		drawLine(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 142 * factor), factor, 0);
		drawLine(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 120 * factor), factor, 9);
		drawLine(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 102 * factor), factor, 18);
		drawLine(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 84 * factor), factor, 27);

		if (this.itemStack != null) {
			this.itemStack.render(graphics, (int) (Game.getInstance().getMouseLocation().x - offsetX), (int) (Game.getInstance().getMouseLocation().y - offsetY), (int) (16 * factor));
		}
	}

	private void drawLine(Graphics2D graphics, int x, int y, double factor, int index) {
		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = Game.getInstance().getPlayer().getInventory().getItems()[i + index];

			int itemX = (int) Math.round(x + i * 18 * factor);
			int size = (int) Math.round(16 * factor);

			slots[i + index] = new AABB(new Vector2D(itemX, y), new Vector2D(itemX + size, y + size));

			if (itemStack != null) {
				itemStack.render(graphics, itemX, y, size);
			}

			Vector2D point = Game.getInstance().getMouseLocation();
			if (point != null && point.x >= itemX && point.x <= itemX + size && point.y >= y && point.y <= y + size) {
				graphics.setColor(new Color(255, 255, 255, 100));
				graphics.fillRect(itemX, y, size, size);
			}
		}
	}

	private void drawPlayer(Graphics2D graphics, double factor, int x, int y) {
		Player player = Game.getInstance().getPlayer();
		double playerX = player.getX();
		double playerY = player.getY();
		double playerFactor = GameRenderer.BLOCK_RESOLUTION * factor * 1.25;

		double positionX = ((49 * factor - player.getCollisionWidth() * playerFactor) / 2 + x + 27 * factor) / playerFactor;
		double positionY = ((70 * factor + player.getCollisionHeight() * playerFactor) / 2 + y + 9 * factor) / playerFactor;

		graphics.scale(playerFactor, playerFactor);
		graphics.translate(-playerX, -playerY);
		graphics.translate(positionX, positionY);
		player.render(graphics);
		graphics.translate(-positionX, -positionY);
		graphics.translate(playerX, playerY);
		graphics.scale(1 / playerFactor, 1 / playerFactor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Vector2D point = Game.getInstance().getMouseLocation();
		if (point == null) {
			return;
		}

		for (int i = 0; i < slots.length; i++) {
			AABB slot = slots[i];
			if (slot != null && slot.contains(point)) {
				ItemStack itemStack = Game.getInstance().getPlayer().getInventory().getItems()[i];
				if (itemStack != null) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (this.itemStack == null) {
							this.itemStack = itemStack;
							Game.getInstance().getPlayer().getInventory().setItem(i, null);
						} else {
							// place item
							if (this.itemStack.getItem().getId() == itemStack.getItem().getId()) {
								if (itemStack.getAmount() + this.itemStack.getAmount() <= ItemStack.MAX_STACK_SIZE) {
									itemStack.setAmount(itemStack.getAmount() + this.itemStack.getAmount());
									this.itemStack = null;
								} else {
									int amount = itemStack.getAmount() + this.itemStack.getAmount() - ItemStack.MAX_STACK_SIZE;
									itemStack.setAmount(ItemStack.MAX_STACK_SIZE);
									this.itemStack.setAmount(amount);
								}
							} else {
								ItemStack temp = this.itemStack;
								this.itemStack = itemStack;
								Game.getInstance().getPlayer().getInventory().setItem(i, temp);
							}
						}
						offsetX = (int) (point.x - slot.getMin().x);
						offsetY = (int) (point.y - slot.getMin().y);
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						if (this.itemStack == null) {
							this.itemStack = new ItemStack(itemStack.getItem(), 1);
							itemStack.setAmount(itemStack.getAmount() - 1);
							if (itemStack.getAmount() <= 0) {
								Game.getInstance().getPlayer().getInventory().setItem(i, null);
							}
							offsetX = (int) (point.x - slot.getMin().x);
							offsetY = (int) (point.y - slot.getMin().y);
						} else if (this.itemStack.getItem().getId() == itemStack.getItem().getId()) {
							this.itemStack.setAmount(this.itemStack.getAmount() + 1);
							itemStack.setAmount(itemStack.getAmount() - 1);
							if (itemStack.getAmount() <= 0) {
								Game.getInstance().getPlayer().getInventory().setItem(i, null);
							}
						}
					}
				} else if (this.itemStack != null) {

					if (e.getButton() == MouseEvent.BUTTON1) {
						Game.getInstance().getPlayer().getInventory().setItem(i, this.itemStack);
						this.itemStack = null;
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						Game.getInstance().getPlayer().getInventory().setItem(i, new ItemStack(this.itemStack.getItem(), 1));
						this.itemStack.setAmount(this.itemStack.getAmount() - 1);
						if (this.itemStack.getAmount() <= 0) {
							this.itemStack = null;
						}
					}
				}
				return;
			}
		}
	}

	@Override
	public boolean doesPauseGame() {
		return false;
	}
}
