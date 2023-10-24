package fr.nicolas.wispy.game.world;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BlockRegistry;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.world.chunks.Chunk;
import fr.nicolas.wispy.game.world.worldgen.WorldGeneration;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class WorldManager {

	public static final int CHUNK_WIDTH = 82;
	public static final int CHUNK_HEIGHT = 100;

	private int leftChunkIndex = -1;
	private final Chunk[] chunks;
	private final Dimensions dimension = Dimensions.OVERWORLD;

	private File worldDir;
	private final Game game;
	private final Camera camera;

	private final BlockRegistry blockRegistry;
	private final WorldGeneration worldGeneration;

	public WorldManager(Game game, Camera camera) {
		this.game = game;
		this.camera = camera;
		this.blockRegistry = new BlockRegistry();
		this.chunks = new Chunk[3];
		this.worldGeneration = new WorldGeneration(this, 0);
	}

	public void loadWorld(String worldName) {
		this.worldDir = new File("Wispy/worlds/" + worldName);

		if (!this.worldDir.isDirectory()) {
			this.worldDir.mkdirs();
		}

		for (int i = 0; i < this.chunks.length; i++) {
			this.chunks[i] = loadChunk(this.leftChunkIndex + i);
		}

		// Player spawnpoint
		this.game.getPlayer().setPos(0, getPlayerSpawnY());
	}

	public double getPlayerSpawnY() {
		int y = 0;
		while (!this.chunks[0].getBlock(0, y).isSolidOrLiquid()) {
			y++;
		}
		return y;
	}

	public void startLoadingChunkThread(Runner runner, GameRenderer gamePanel) {
		Thread loadNextMap = new Thread(() -> {
            while (true) {
                double chunkCenterX;

				Chunk chunkLeft = this.chunks[0];
				Chunk chunkRight = this.chunks[2];

                if (chunkLeft != null) {
					int chunkX = this.leftChunkIndex * chunkLeft.getWidth();
					chunkCenterX = (chunkX + chunkLeft.getWidth() / 2.0) - game.getPlayer().getX();
                    if (chunkCenterX >= 0) {
                        saveChunk(chunkRight, this.leftChunkIndex + 2);
						this.chunks[2] = this.chunks[1];
						this.chunks[1] = this.chunks[0];

						this.leftChunkIndex--;
						this.chunks[0] = loadChunk(this.leftChunkIndex);
                    }
                }

				if (chunkRight != null) {
					int chunkX = (this.leftChunkIndex + 2) * chunkRight.getWidth();
					chunkCenterX = (chunkX + chunkRight.getWidth() / 2.0) - game.getPlayer().getX();
					if (chunkCenterX <= gamePanel.getWidth() / (double) gamePanel.getBlockSize()) {
						saveChunk(chunkLeft, this.leftChunkIndex);

						this.chunks[0] = this.chunks[1];
						this.chunks[1] = this.chunks[2];

						this.leftChunkIndex++;
						this.chunks[2] = loadChunk(this.leftChunkIndex + 2);
					}
				}

                int waitTime = (int) runner.getWaitTime();
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
		loadNextMap.start();
	}

	private void saveChunk(Chunk chunk, int index) {
		File dimensionFolder = new File(worldDir, "" + dimension.getId());

		if (!dimensionFolder.isDirectory()) {
			dimensionFolder.mkdirs();
		}

		try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(new File(dimensionFolder, index + ".chunk").toPath()))) {
			dos.writeInt(chunk.getBlocks().length);
			for (Block block : chunk.getBlocks()) {
				byte[] chunkBytes = block.toBytes();
				dos.writeInt(chunkBytes.length);
				dos.write(chunkBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Chunk loadChunk(int index) {
		File dimensionFolder = new File(worldDir, "" + dimension.getId());
		File chunkFile = new File(dimensionFolder, index + ".chunk");

		if (chunkFile.isFile()) {
			return loadChunkFromFile(index);
		}

		Chunk chunk = worldGeneration.generateChunk(index);
		saveChunk(chunk, index);
		return chunk;
	}

	private Chunk loadChunkFromFile(int index) {
		File dimensionFolder = new File(worldDir, "" + dimension.getId());
		File chunkFile = new File(dimensionFolder, index + ".chunk");

		Chunk chunk = new Chunk(this);
		Block[] blocks = new Block[CHUNK_WIDTH * CHUNK_HEIGHT];
		chunk.setBlocks(blocks);

		try (DataInputStream dis = new DataInputStream(Files.newInputStream(chunkFile.toPath()))) {
			int arrayLength = dis.readInt();
			for (int i = 0; i < arrayLength; i++) {
				int blockBytesLength = dis.readInt();
				byte[] blockBytes = new byte[blockBytesLength];
				dis.readFully(blockBytes);
				blocks[i] = Block.fromBytes(blockBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();

			chunk = worldGeneration.generateChunk(index);
		}

		return chunk;
	}

	public void renderChunks(Graphics2D g, double width, double height) {
		for (int i = 0; i < this.chunks.length; i++) {
			renderChunk(g, this.chunks[i], this.leftChunkIndex + i, width, height);
		}
	}

	private void renderChunk(Graphics2D g, Chunk chunk, int chunkIndex, double width, double height) {
		if (chunk == null || chunk.getBlocks() == null) {
			return;
		}

		int chunkWidth = chunk.getWidth();
		int chunkX = chunkWidth * chunkIndex;

		if (chunkX + chunkWidth < camera.getX() || chunkX > camera.getX() + width) {
			return;
		}

		int chunkHeight = chunk.getHeight();
		int chunkY = 0;

		if (chunkY + chunkHeight < camera.getY() || chunkY > camera.getY() + height) {
			return;
		}

		int startingPointX = (int) Math.max(0, camera.getX() - chunkX);
		int startingPointY = (int) Math.max(0, camera.getY() - chunkY);

		int[] lightLevels = new int[chunkWidth];
		int[] fluidLevels = new int[chunkWidth];

		double yOffset = camera.getY() - camera.getBlockY();

		for (int x = startingPointX; x < Math.min(camera.getBlockX() + 1 + width - chunkX, chunkWidth); x++) {
			int blockX = x + chunkX;

			for (int y = startingPointY; y < Math.min(camera.getBlockY() + 1 + height - chunkY, chunkHeight); y++) {
				Block block = chunk.getBlock(x, y);
				if (block.getType() != Blocks.AIR) {
					if (block.isSolid() && !block.isBackgroundBlock() && lightLevels[x] == 0) {
						lightLevels[x] = y;
					} else if (block.isLiquid() && !block.isBackgroundBlock() && fluidLevels[x] == 0) {
						fluidLevels[x] = y;
					}

					g.drawImage(block.getTexture(), blockX, y, 1, 1, null);

					if (block.isBackgroundBlock()) {
						g.setColor(new Color(0, 0, 0, 50));
						g.fillRect(blockX, y, 1, 1);
					} else if (lightLevels[x] != 0 && y >= lightLevels[x]) {
						int minOpacity = 0;
						if (fluidLevels[x] != 0 && y >= fluidLevels[x]) {
							minOpacity = Math.max(0, Math.min(255, (int) (((y - (fluidLevels[x] == startingPointY ? yOffset : 0) - fluidLevels[x])) * 255.0 / 24)));
						}

						int opacity = (int) (((y + (lightLevels[x] == startingPointY ? yOffset : 0) - lightLevels[x])) * 255.0 / 10);
						g.setColor(new Color(0, 0, 0, Math.max(minOpacity, Math.min(255, opacity))));
						g.fillRect(blockX, y, 1, 1);
					} else if (block.isLiquid() && fluidLevels[x] != 0 && y >= fluidLevels[x]) {
						int opacity = (int) (((y - (fluidLevels[x] == startingPointY ? yOffset : 0) - fluidLevels[x])) * 255.0 / 24);
						g.setColor(new Color(0, 0, 0, Math.max(0, Math.min(255, opacity))));
						g.fillRect(blockX, y, 1, 1);
					}
				}
			}
		}
	}

	public void renderSelection(Graphics2D g, int blockSize, Point mouseLocation) {
		int blockX = (int) Math.floor(camera.getX() + mouseLocation.x / (double) blockSize);
		int blockY = (int) Math.floor(camera.getY() + mouseLocation.y / (double) blockSize);

		int chunkIndex = (int) Math.floor(blockX / (double) CHUNK_WIDTH);

		if (chunkIndex < this.leftChunkIndex || chunkIndex >= this.leftChunkIndex + this.chunks.length) {
			return;
		}

		Chunk chunk = this.chunks[chunkIndex - this.leftChunkIndex];

		int chunkWidth = chunk.getWidth();
		int chunkX = chunkWidth * chunkIndex;

		if (blockX < chunkX || blockX >= chunkX + chunkWidth) {
			return;
		}

		int chunkHeight = chunk.getHeight();
		int chunkY = 0;

		if (blockY < chunkY || blockY >= chunkY + chunkHeight) {
			return;
		}

		Block block = chunk.getBlock(blockX - chunkX, blockY - chunkY);

		if (block.getType() == Blocks.AIR) {
			return;
		}

		g.setColor(new Color(0, 0, 0, 120));
		g.setStroke(new BasicStroke(3.0F / blockSize));
		g.drawRect(blockX, blockY, 1, 1);
	}

	public BlockRegistry getBlockRegistry() {
		return this.blockRegistry;
	}

	public Chunk[] getChunks() {
		return this.chunks;
	}

	public int getLeftChunkIndex() {
		return this.leftChunkIndex;
	}
}
