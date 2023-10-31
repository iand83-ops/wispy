package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.registry.Materials;
import fr.nicolas.wispy.game.entities.EntityItem;
import fr.nicolas.wispy.game.items.registry.Items;
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

	private final Blocks type;
	private Blocks decorationType = Blocks.AIR;
	private Blocks originalType = Blocks.AIR;
	private final Materials material;
	private final Items itemType;

	private final double width;
	private final double height;

	private final BufferedImage texture;

	private boolean backgroundBlock = false;

	private long tickPlaced;

	public Block(Blocks type, Items itemType) {
		this(type, Materials.SOLID, itemType, 1, 1);
	}

	public Block(Blocks type, Materials material, Items itemType) {
		this(type, material, itemType, 1, 1);
	}

	public Block(Blocks type, Materials material, Items itemType, double width, double height) {
		this.type = type;
		this.material = material;
		this.itemType = itemType;
		this.width = width;
		this.height = height;

		this.texture = type == Blocks.AIR ? null : Assets.get("blocks/" + type.name().toLowerCase());

		this.tickPlaced = Game.getInstance().getGameTick();
	}

	public void tick(WorldManager worldManager, int x, int y, long gameTick) {

	}

	public void onBreak(WorldManager worldManager, int x, int y) {
		if (this.itemType == null) {
			return;
		}

		EntityItem item = new EntityItem(worldManager, this.itemType.getItem().copy());
		item.setPos(x + 0.25 + (new Random().nextDouble() - 0.5) / 2, y + 0.75);
		worldManager.addEntity(item);
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

	public boolean canReplace() {
		return this.getType() == Blocks.AIR || this.isBackgroundBlock() || this.isLiquid();
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

	public Block copyClass() {
		return new Block(this.type, this.material, this.itemType, this.width, this.height);
	}

	public Block copy() {
		Block clone = copyClass();
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
