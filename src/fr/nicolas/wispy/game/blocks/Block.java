package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.utils.Assets;

import java.awt.image.BufferedImage;

public class Block {

	private final Blocks type;
	private final double width;
	private final double height;

	private final BufferedImage texture;

	public Block(Blocks type, double width, double height) {
		this.type = type;
		this.width = width;
		this.height = height;

		this.texture = type == Blocks.AIR ? null : Assets.get("blocks/" + type.name().toLowerCase());
	}

	public Blocks getType() {
		return this.type;
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

}
