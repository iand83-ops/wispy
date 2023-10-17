package fr.nicolas.wispy.game.blocks;

import fr.nicolas.wispy.game.utils.Assets;

import java.awt.image.BufferedImage;

public class Block {

	private final Blocks type;
	private final float width;
	private final float height;

	private final BufferedImage texture;

	public Block(Blocks type, float width, float height) {
		this.type = type;
		this.width = width;
		this.height = height;

		this.texture = type == Blocks.AIR ? null : Assets.get("blocks/" + type.name().toLowerCase());
	}

	public Blocks getType() {
		return this.type;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public BufferedImage getTexture() {
		return this.texture;
	}

}
