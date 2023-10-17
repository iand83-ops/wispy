package fr.nicolas.wispy.panels.components.game;

import fr.nicolas.wispy.panels.GamePanel;
import fr.nicolas.wispy.panels.GamePanel.BlockID;

import java.awt.*;

public class Block extends Rectangle {

	private final BlockID blockID;

	public Block(BlockID blockID) {
		this.blockID = blockID;

		if (blockID == BlockID.STONE || blockID == BlockID.DIRT || blockID == BlockID.GRASS || blockID == BlockID.SAND) {
			width = GamePanel.BLOCK_SIZE;
			height = GamePanel.BLOCK_SIZE;
		}
	}

	public void paint(Graphics g, int newX, int newY, int newBlockWidth, int newBlockHeight) {
		g.drawImage(blockID.getImg(), newX, newY, newBlockWidth, newBlockHeight, null);
	}

}
