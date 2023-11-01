package fr.nicolas.wispy.ui.menu.inventory;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.ui.menu.Menu;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class InventoryMenu extends Menu {

	protected final BufferedImage inventoryTexture;
	protected final AABB[] slots;
	protected final ItemStack[] topSlots;
	protected final ItemStack[] immutableSlots;

	private ItemStack itemStack;
	private int offsetX;
	private int offsetY;

	private final ArrayList<Integer> draggingSlots = new ArrayList<>();
	private final ArrayList<Integer> draggingInitialAmount = new ArrayList<>();
	private int draggingButton = -1;

	public InventoryMenu(int topSlots, int immutableSlots, BufferedImage inventoryTexture) {
		this.topSlots = new ItemStack[topSlots];
		this.immutableSlots = new ItemStack[immutableSlots];
		this.inventoryTexture = inventoryTexture;

		this.slots = new AABB[Game.getInstance().getPlayer().getInventory().getSize() + topSlots + immutableSlots];
	}

	@Override
	public void close() {
		if (itemStack != null) {
			Game.getInstance().getPlayer().getInventory().addItemStack(itemStack);
		}

		for (ItemStack stack : topSlots) {
			if (stack != null) {
				Game.getInstance().getPlayer().getInventory().addItemStack(stack);
			}
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

		drawBeforeSlots(graphics, x, y, factor);

		drawSlots(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 142 * factor), factor, 0, 9);
		drawSlots(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 120 * factor), factor, 9, 9);
		drawSlots(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 102 * factor), factor, 18, 9);
		drawSlots(graphics, (int) Math.round(x + 8 * factor), (int) Math.round(y + 84 * factor), factor, 27, 9);

		drawTopSlots(graphics, x, y, factor);

		if (this.itemStack != null && this.itemStack.getAmount() > 0) {
			this.itemStack.render(graphics, (int) (Game.getInstance().getMouseLocation().x - offsetX), (int) (Game.getInstance().getMouseLocation().y - offsetY), (int) (16 * factor));
		}
	}

	public abstract void drawBeforeSlots(Graphics2D graphics, int x, int y, double factor);

	public abstract void drawTopSlots(Graphics2D graphics, int x, int y, double factor);

	protected void drawSlots(Graphics2D graphics, int x, int y, double factor, int index, int length) {
		for (int i = 0; i < length; i++) {
			ItemStack itemStack = getItemStack(i + index);

			int itemX = (int) Math.round(x + i * 18 * factor);
			int size = (int) Math.round(16 * factor);

			slots[i + index] = new AABB(new Vector2D(itemX, y), new Vector2D(itemX + size, y + size));

			if (itemStack != null) {
				itemStack.render(graphics, itemX, y, size);
			}

			Vector2D point = Game.getInstance().getMouseLocation();
			if (draggingSlots.contains(index + i) || point != null && point.x >= itemX && point.x <= itemX + size && point.y >= y && point.y <= y + size) {
				graphics.setColor(new Color(255, 255, 255, 100));
				graphics.fillRect(itemX, y, size, size);
			}
		}
	}

	protected void drawPlayer(Graphics2D graphics, int x, int y, double factor) {
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
				boolean isImmutable = i >= Game.getInstance().getPlayer().getInventory().getSize() + topSlots.length;

				ItemStack itemStack = getItemStack(i);

				if (itemStack != null) {
					if (e.getButton() == MouseEvent.BUTTON1 || isImmutable) { // Left Click
						if (this.itemStack == null || isImmutable) { // Pick Items
							if (this.itemStack != null && !e.isShiftDown()) { // Stack Picked Items
								if (this.itemStack.getItem().canStack() && this.itemStack.getItem().getId() == itemStack.getItem().getId() && itemStack.getAmount() + this.itemStack.getAmount() <= ItemStack.MAX_STACK_SIZE) {
									this.itemStack.setAmount(itemStack.getAmount() + this.itemStack.getAmount());
									setItemStack(i, null);
									break;
								}
							} else {
								if (e.isShiftDown()) { // Bring Items
									do {
										if (Game.getInstance().getPlayer().getInventory().addItemStack(itemStack)) {
											setItemStack(i, null);
										}
										itemStack = getItemStack(i);
									} while (isImmutable && itemStack != null);
									break;
								} else { // Pick Items
									this.itemStack = itemStack;
									setItemStack(i, null);
								}
							}
						} else { // Place Items
							if (this.itemStack.getItem().canStack() && this.itemStack.getItem().getId() == itemStack.getItem().getId()) { // Stack Items
								if (itemStack.getAmount() + this.itemStack.getAmount() <= ItemStack.MAX_STACK_SIZE) {
									draggingSlots.add(i);
									draggingInitialAmount.add(itemStack.getAmount());

									itemStack.setAmount(itemStack.getAmount() + this.itemStack.getAmount());
									this.itemStack.setAmount(0);
									draggingButton = e.getButton();
								} else { // Stack Items and Split
									draggingSlots.add(i);
									draggingInitialAmount.add(itemStack.getAmount());

									int amount = itemStack.getAmount() + this.itemStack.getAmount() - ItemStack.MAX_STACK_SIZE;
									itemStack.setAmount(ItemStack.MAX_STACK_SIZE);
									this.itemStack.setAmount(amount);
									draggingButton = e.getButton();
								}
							} else { // Swap Items
								ItemStack temp = this.itemStack;
								this.itemStack = itemStack;
								setItemStack(i, temp);
							}
						}
						offsetX = (int) (point.x - slot.getMin().x);
						offsetY = (int) (point.y - slot.getMin().y);
					} else if (e.getButton() == MouseEvent.BUTTON3) { // Right Click
						if (this.itemStack == null) { // Pick 1 Item
							this.itemStack = new ItemStack(itemStack.getItem(), 1);
							itemStack.setAmount(itemStack.getAmount() - 1);
							if (itemStack.getAmount() <= 0) {
								setItemStack(i, null);
							}
							offsetX = (int) (point.x - slot.getMin().x);
							offsetY = (int) (point.y - slot.getMin().y);
						} else if (this.itemStack.getItem().canStack() && this.itemStack.getItem().getId() == itemStack.getItem().getId()) { // Place 1 Item
							itemStack.setAmount(itemStack.getAmount() + 1);
							this.itemStack.setAmount(this.itemStack.getAmount() - 1);
						}
					}
				} else if (this.itemStack != null) {
					if (e.getButton() == MouseEvent.BUTTON1) { // Left Click Place Items
						draggingSlots.add(i);
						draggingInitialAmount.add(0);

						setItemStack(i, new ItemStack(this.itemStack.getItem(), this.itemStack.getAmount()));
						this.itemStack.setAmount(0);
						draggingButton = e.getButton();
					} else if (e.getButton() == MouseEvent.BUTTON3) { // Right Click Place 1 Item
						draggingSlots.add(i);
						draggingInitialAmount.add(0);

						setItemStack(i, new ItemStack(this.itemStack.getItem(), 1));
						this.itemStack.setAmount(this.itemStack.getAmount() - 1);
						draggingButton = e.getButton();
					}
				}
				break;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Vector2D point = Game.getInstance().getMouseLocation();
		if (point == null || draggingSlots.isEmpty()) {
			return;
		}

		for (int i = 0; i < slots.length; i++) {
			AABB slot = slots[i];
			if (slot != null && slot.contains(point)) {
				if (draggingButton == MouseEvent.BUTTON1) { // Left Click
					if (!draggingSlots.contains(i)) {
						int slotsCount = draggingSlots.size() + 1;

						int initialAmount = this.itemStack != null ? Math.max(this.itemStack.getAmount(), 0) : 0;

						for (int slotIndex : draggingSlots) {
							initialAmount += getItemStack(slotIndex).getAmount() - draggingInitialAmount.get(draggingSlots.indexOf(slotIndex));
						}

						int itemsToDistribute = initialAmount / slotsCount;
						int excess = initialAmount % slotsCount;

						if (slotsCount > initialAmount) {
							break;
						}

						ItemStack itemStack = getItemStack(i);
						if (itemStack != null) {
							draggingInitialAmount.add(itemStack.getAmount());
						} else {
							draggingInitialAmount.add(0);
						}

						if (itemsToDistribute != 1 || excess != 0) {
							if (this.itemStack != null) {
								this.itemStack.setAmount(excess);
							}
						} else {
							if (this.itemStack != null) {
								this.itemStack.setAmount(0);
							}
						}

						if (itemsToDistribute == 0) {
							break;
						}

						draggingSlots.add(i);

						for (int slotIndex : draggingSlots) {
							distributeItemToSlot(slotIndex, itemsToDistribute, draggingSlots.indexOf(slotIndex));
						}
					}
				} else if (draggingButton == MouseEvent.BUTTON3) { // Right Click
					if (draggingSlots.get(draggingSlots.size() - 1) != i) {
						if (this.itemStack != null) { // Place 1 Item
							ItemStack itemStack = getItemStack(i);
							if (itemStack == null || (itemStack.getItem().canStack() && this.itemStack.getItem().getId() == itemStack.getItem().getId())) {
								if (itemStack != null) {
									itemStack.setAmount(itemStack.getAmount() + 1);
								} else {
									setItemStack(i, new ItemStack(this.itemStack.getItem(), 1));
								}
								this.itemStack.setAmount(this.itemStack.getAmount() - 1);
								if (this.itemStack.getAmount() <= 0) {
									this.itemStack = null;
								}
							}
							draggingSlots.add(i);
							draggingInitialAmount.add(itemStack == null ? 0 : itemStack.getAmount());
						}
					}
				}

				break;
			}
		}
	}

	private void distributeItemToSlot(int slotIndex, int amount, int draggingIndex) {
		ItemStack slotItemStack = getItemStack(slotIndex);
		if (slotItemStack == null || (slotItemStack.getItem().canStack() && this.itemStack.getItem().getId() == slotItemStack.getItem().getId())) {
			if (slotItemStack != null) {
				slotItemStack.setAmount(draggingInitialAmount.get(draggingIndex) + amount);
			} else {
				setItemStack(slotIndex, new ItemStack(this.itemStack.getItem(), draggingInitialAmount.get(draggingIndex) + amount));
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (draggingButton == e.getButton()) {
			draggingSlots.clear();
			draggingInitialAmount.clear();
			draggingButton = -1;
		}

		if (this.itemStack != null && this.itemStack.getAmount() <= 0) {
			this.itemStack = null;
		}
	}

	public ItemStack getItemStack(int index) {
		if (index < Game.getInstance().getPlayer().getInventory().getSize()) {
			return Game.getInstance().getPlayer().getInventory().getItems()[index];
		}

		index -= Game.getInstance().getPlayer().getInventory().getSize();

		if (index < this.topSlots.length) {
			return this.topSlots[index];
		}

		index -= this.topSlots.length;

		if (index < this.immutableSlots.length) {
			return this.immutableSlots[index];
		}

		index -= this.immutableSlots.length;

		return null;
	}

	public void setItemStack(int index, ItemStack itemStack) {
		if (index < Game.getInstance().getPlayer().getInventory().getSize()) {
			Game.getInstance().getPlayer().getInventory().getItems()[index] = itemStack;
			return;
		}

		index -= Game.getInstance().getPlayer().getInventory().getSize();

		if (index < this.topSlots.length) {
			this.topSlots[index] = itemStack;
			return;
		}

		index -= this.topSlots.length;

		if (index < this.immutableSlots.length) {
			this.immutableSlots[index] = itemStack;
			onSetImmutable(index, itemStack);
			return;
		}

		index -= this.immutableSlots.length;
	}

	public abstract void onSetImmutable(int index, ItemStack itemStack);

	@Override
	public boolean doesPauseGame() {
		return false;
	}
}
