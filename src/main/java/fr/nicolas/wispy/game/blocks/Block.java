package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.registery.Blocks;
import fr.nicolas.wispy.game.blocks.registery.Materials;
import fr.nicolas.wispy.game.utils.Assets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Block {

	private final Blocks type;
	private final Materials material;
	private final double width;
	private final double height;

	private final BufferedImage texture;

	private boolean backgroundBlock = false;

	public Block(Blocks type) {
		this(type, Materials.SOLID, 1, 1);
	}

	public Block(Blocks type, Materials material) {
		this(type, material, 1, 1);
	}

	public Block(Blocks type, Materials material, double width, double height) {
		this.type = type;
		this.material = material;
		this.width = width;
		this.height = height;

		this.texture = type == Blocks.AIR ? null : Assets.get("blocks/" + type.name().toLowerCase());
	}

	public byte[] toBytes() throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(stream)) {
			out.writeInt(this.type.getId());
			write(out);
			return stream.toByteArray();
		}
	}

	public static Block fromBytes(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);

		int id = buffer.getInt();

		Block block = Game.getInstance().getWorldManager().getBlockRegistry().getBlock(id);
		block.read(buffer);

		return block;
	}

	protected void read(ByteBuffer buffer) {
		this.backgroundBlock = buffer.get() == 1;
	}

	protected void write(DataOutputStream out) throws IOException {
		out.write((byte) (this.backgroundBlock ? 1 : 0));
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
		return this.material != Materials.NON_SOLID;
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

	public Block copyClass() {
		return new Block(this.type, this.material, this.width, this.height);
	}

	public Block copy() {
		Block clone = copyClass();
		clone.setBackgroundBlock(this.backgroundBlock);
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
