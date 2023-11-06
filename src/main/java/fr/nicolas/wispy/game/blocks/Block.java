package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.registry.Materials;
import fr.nicolas.wispy.game.entities.EntityItem;
import fr.nicolas.wispy.game.items.Item;
import fr.nicolas.wispy.game.items.registry.Items;
import fr.nicolas.wispy.game.items.registry.Tools;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.WorldManager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Random;

public class Block {

	private Blocks type = Blocks.AIR;
	private Blocks decorationType = Blocks.AIR;
	private Blocks originalType = Blocks.AIR;
	private Materials material = Materials.SOLID;
	private Items itemType;
	private Blocks blocksItemType;
	private Tools tools;

	private int durability = 100;

	private final double width;
	private final double height;

	private BufferedImage texture;

	private boolean backgroundBlock = false;

	private final long tickPlaced;

	public Block() {
		this(1, 1);
	}

	public Block(double width, double height) {
		this.width = width;
		this.height = height;

		this.tickPlaced = Game.getInstance().getGameTick();
	}

	public Block type(Blocks type) {
		this.type = type;
		this.texture = type == Blocks.AIR ? null : Assets.get("blocks/" + type.name().toLowerCase());
		return this;
	}

	public Block material(Materials material) {
		this.material = material;
		return this;
	}

	public Block itemType(Items itemType) {
		this.itemType = itemType;
		return this;
	}

	public Block blocksItemType(Blocks blocksItemType) {
		this.blocksItemType = blocksItemType;
		if (blocksItemType != null) {
			this.itemType = Items.BLOCK;
		}
		return this;
	}

	public Block tools(Tools tools) {
		this.tools = tools;
		return this;
	}

	public Block durability(int durability) {
		this.durability = durability;
		return this;
	}

	public void tick(WorldManager worldManager, int x, int y, long gameTick) {

	}

	public void onBreak(WorldManager worldManager, int x, int y) {
		if (this.itemType == null) {
			return;
		}

		Item item = this.itemType.getItem();

		if (this.blocksItemType != null) {
			item = worldManager.getItemRegistry().getItem(this.blocksItemType.getId());
		}

		EntityItem entityItem = new EntityItem(worldManager, item.copy());
		entityItem.setPos(x + 0.25 + (new Random().nextDouble() - 0.5) / 2, y + 0.75);
		worldManager.addEntity(entityItem);
	}

	public void onRightClick(WorldManager worldManager, int x, int y) {

	}

	public byte[] toBytes() throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
			out.writeInt(this.type.getId());
			out.writeInt(this.decorationType.getId());
			out.writeInt(this.originalType.getId());
			write(out);
			return stream.toByteArray();
		}
	}

	public static Block fromBytes(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);

		int id = buffer.getInt();
		int decorationId = buffer.getInt();
		int originalId = buffer.getInt();

		Block block = Game.getInstance().getWorldManager().getBlockRegistry().getBlock(id);
		block.setDecorationType(Game.getInstance().getWorldManager().getBlockRegistry().getBlock(decorationId).getType());
		block.setOriginalType(Game.getInstance().getWorldManager().getBlockRegistry().getBlock(originalId).getType());
		block.read(buffer);

		return block;
	}

	protected void read(ByteBuffer buffer) {
		this.backgroundBlock = buffer.get() == 1;
	}

	protected void write(DataOutputStream out) throws IOException {
		out.write(this.backgroundBlock ? 1 : 0);
	}

	public Blocks getType() {
		return this.type;
	}

	public Materials getMaterial() {
		return this.material;
	}

	public double getWidth() {
		return this.width;
	}

	public double getHeight() {
		return this.height;
	}

	public BufferedImage getTexture() {
		return this.texture;
	}

	public boolean isSolid() {
		return this.material == Materials.SOLID || this.material == Materials.SOLID_GRAVITY;
	}

	public boolean isSolidOrLiquid() {
		return this.material != Materials.NON_SOLID && this.material != Materials.NON_SOLID_TRANSPARENT;
	}

	public boolean isTransparent() {
		return type == Blocks.AIR || this.material == Materials.NON_SOLID_TRANSPARENT || this.material == Materials.TRANSPARENT;
	}

	public boolean isLiquid() {
		return this.material == Materials.LIQUID;
	}

	public int getId() {
		return this.type.getId();
	}

	public void setBackgroundBlock(boolean backgroundBlock) {
		this.backgroundBlock = backgroundBlock;
	}

	public boolean isBackgroundBlock() {
		return this.backgroundBlock;
	}

	public boolean renderAsSolidColor() {
		return false;
	}

	public int getSolidColor() {
		return 0;
	}

	public boolean canBreak() {
		return this.getType() != Blocks.AIR && this.getType() != Blocks.BEDROCK && !this.isBackgroundBlock();
	}

	public boolean takeBreakPriority() {
		return canBreak() && !isTransparent() && !isBackgroundBlock();
	}

	public boolean canReplace() {
		return this.getType() == Blocks.AIR || this.isBackgroundBlock() || this.isLiquid();
	}

	public boolean takeReplacePriority() {
		return canReplace() && !isBackgroundBlock();
	}

	public long getTickPlaced() {
		return this.tickPlaced;
	}

	public void setDecorationType(Blocks decorationType) {
		this.decorationType = decorationType;
	}

	public Blocks getDecorationType() {
		return this.decorationType;
	}

	public void setOriginalType(Blocks originalType) {
		this.originalType = originalType;
	}

	public Blocks getOriginalType() {
		return this.originalType;
	}

	public Items getItemType() {
		return this.itemType;
	}

	public void setBlocksItemType(Blocks blocksItemType) {
		this.blocksItemType = blocksItemType;
	}

	public Tools getTools() {
		return this.tools;
	}

	public int getDurability() {
		return this.durability;
	}

	public Block copyClass() {
		return new Block(this.width, this.height);
	}

	public Block copy() {
		Block clone = copyClass()
				.type(this.type)
				.material(this.material)
				.itemType(this.itemType)
				.blocksItemType(this.blocksItemType)
				.tools(this.tools)
				.durability(this.durability);

		clone.setBackgroundBlock(this.backgroundBlock);
		clone.setDecorationType(this.decorationType);
		clone.setOriginalType(this.originalType);
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Block block = (Block) o;
		return backgroundBlock == block.backgroundBlock && type == block.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, backgroundBlock);
	}
}
