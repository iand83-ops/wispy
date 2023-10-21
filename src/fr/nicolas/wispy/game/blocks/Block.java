package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.utils.Assets;

import java.awt.image.BufferedImage;

public class Block {

	private final Blocks type;
	private final Materials material;
	private final double width;
	private final double height;

	private final BufferedImage texture;

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

}
