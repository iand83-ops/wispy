package fr.nicolas.wispy.game.world;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BlockRegistry;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class WorldManager {

	private int leftChunkIndex = -1;
	private int[][][] chunks;

	private File worldDir;
	private final Game game;
	private final Camera camera;

	// Random generation variables
	private int state = 0;
	private int changeStateNum = 5;
	private int currentNum = 0;
	private int lastY = 10;
	private int lastState = 0;

	private final BlockRegistry blockRegistry;

	public WorldManager(Game game, Camera camera) {
		this.game = game;
		this.camera = camera;
		this.blockRegistry = new BlockRegistry();
		this.chunks = new int[3][][];
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
		while (this.chunks[0][0][y] == Blocks.AIR.getId()) {
			y++;
		}
		return y;
	}

	public void startLoadingChunkThread(Runner runner, GameRenderer gamePanel) {
		Thread loadNextMap = new Thread(() -> {
            while (true) {
                double chunkCenterX;

				int[][] chunkLeft = this.chunks[0];
				int[][] chunkRight = this.chunks[2];

                if (chunkRight != null) {
					int chunkX = (this.leftChunkIndex + 2) * chunkRight.length;
                    chunkCenterX = (chunkX + chunkRight.length / 2.0) - game.getPlayer().getX();
                    if (chunkCenterX <= gamePanel.getWidth() / (double) gamePanel.getBlockSize()) {
                        saveChunk(chunkLeft, this.leftChunkIndex);

						this.chunks[0] = this.chunks[1];
						this.chunks[1] = this.chunks[2];

						this.leftChunkIndex++;
						this.chunks[2] = loadChunk(this.leftChunkIndex + 2);
                    }
                }

                if (chunkLeft != null) {
					int chunkX = this.leftChunkIndex * chunkLeft.length;
					chunkCenterX = (chunkX + chunkLeft.length / 2.0) - game.getPlayer().getX();
                    if (chunkCenterX >= gamePanel.getWidth() / (double) gamePanel.getBlockSize()) {
                        saveChunk(chunkRight, this.leftChunkIndex + 2);

						this.chunks[2] = this.chunks[1];
						this.chunks[1] = this.chunks[0];

						this.leftChunkIndex--;
						this.chunks[0] = loadChunk(this.leftChunkIndex);
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

	private void saveChunk(int[][] mapToSave, int num) {
		try {
			ObjectOutputStream objectOutputS = new ObjectOutputStream(Files.newOutputStream(new File(this.worldDir, num + ".chunk").toPath()));
			objectOutputS.writeObject(mapToSave);
			objectOutputS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int[][] loadChunk(int index) {
		File chunkFile = new File(worldDir, index + ".chunk");

		if (chunkFile.isFile()) {
			return loadChunkFromFile(chunkFile);
		}

		int[][] chunk = generateChunk(index);
		saveChunk(chunk, index);
		return chunk;
	}

	private int[][] loadChunkFromFile(File chunkFile) {
		int[][] loadedMap = null;
		try {
			ObjectInputStream objectInputS = new ObjectInputStream(Files.newInputStream(chunkFile.toPath()));

			try {
				loadedMap = (int[][]) objectInputS.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			objectInputS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return loadedMap;
	}

	private int[][] generateChunk(int index) {
		int[][] mapToGenerate = new int[82][100];

		int newY = 0;

		for (int x = 0; x < mapToGenerate.length; x++) {
			// TODO: Algo de génération
			// TODO: Actuellement: génération map par map au lieu de l'ensemble des maps
			// d'un coup ... (problèmes si grottes, montagnes, bâtiments ...)

			if (random(1, 2) == 1) {
				if (state == 0) {
					newY = random(lastY - 1, lastY + 1);
				} else if (state == 1) {
					if (random(1, 3) == 1) {
						newY = random(lastY, lastY + 3);
					} else {
						newY = random(lastY, lastY + 2);
					}
				} else if (state == 2) {
					if (random(1, 3) == 1) {
						newY = random(lastY, lastY - 3);
					} else {
						newY = random(lastY, lastY - 2);
					}
				}
			} else {
				newY = lastY;
			}

			if (currentNum == changeStateNum) {
				currentNum = 0;
				changeStateNum = random(3, 7);

				if (random(1, 3) == 1) {
					state = random(1, 2);
				} else {
					state = 0;
				}

				if (lastState == state) {
					if (random(1, 3) != 1) {
						if (state == 1) {
							state = 2;
						} else if (state == 2) {
							state = 1;
						}
					}
				}

				lastState = state;
			} else {
				currentNum++;
			}

			if (newY > 25) { // Profondeur y max
				newY = 25;
			} else if (newY < 10) { // Hauteur y max
				newY = 10;
			}

			lastY = newY;
			mapToGenerate[x][newY] = Blocks.GRASS.getId();

			for (int y = newY + 1; y < mapToGenerate[0].length; y++) {
				if (y < newY + 3) {
					mapToGenerate[x][y] = Blocks.DIRT.getId();
				} else {
					mapToGenerate[x][y] = Blocks.STONE.getId();
				}
			}
		}

		return mapToGenerate;
	}

	private int random(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}

	public void renderChunks(Graphics g, int width, int height, int blockWidth) {
		for (int i = 0; i < this.chunks.length; i++) {
			renderChunk(g, this.chunks[i], this.leftChunkIndex + i, width, height, blockWidth);
		}
	}

	public void renderSelections(Graphics g, int width, Point mouseLocation) {
		for (int i = 0; i < this.chunks.length; i++) {
			renderSelection(g, this.chunks[i], this.leftChunkIndex + i, width, mouseLocation);
		}
	}

	private void renderChunk(Graphics g, int[][] chunk, int chunkIndex, int width, int height, int blockSize) {
		if (chunk == null) {
			return;
		}

		int chunkWidth = chunk.length;
		int chunkX = chunkWidth * chunkIndex;

		double screenWidth = width / (double) blockSize;

		if (chunkX + chunkWidth < camera.getX() || chunkX > camera.getX() + screenWidth) {
			return;
		}

		int chunkHeight = chunk[0].length;
		int chunkY = 0;

		double screenHeight = height / (double) blockSize;

		if (chunkY + chunkHeight < camera.getY() || chunkY > camera.getY() + screenHeight) {
			return;
		}

		int startingPointX = (int) Math.max(0, camera.getX() - chunkX);
		int startingPointY = (int) Math.max(0, camera.getY() - chunkY);

		for (int x = startingPointX; x < Math.min(camera.getX() + screenWidth - chunkX, chunk.length); x++) {
			int renderX = (int) ((x + chunkX - camera.getX()) * blockSize);

			for (int y = startingPointY; y < Math.min(camera.getY() + screenHeight - chunkY, chunk[0].length); y++) {
				int renderY = (int) ((y - camera.getY()) * blockSize);

				if (chunk[x][y] != Blocks.AIR.getId()) {
					Block block = this.blockRegistry.getBlock(chunk[x][y]);
					g.drawImage(block.getTexture(), renderX, renderY, blockSize, blockSize, null);
				}
			}
		}
	}

	public void renderSelection(Graphics g, int[][] chunk, int chunkIndex, int blockSize, Point mouseLocation) {
		if (chunk == null) {
			return;
		}

		int blockX = (int) Math.floor(camera.getX() + mouseLocation.x / (double) blockSize);
		int blockY = (int) Math.floor(camera.getY() + mouseLocation.y / (double) blockSize);

		int chunkWidth = chunk.length;
		int chunkX = chunkWidth * chunkIndex;

		if (blockX < chunkX || blockX >= chunkX + chunkWidth) {
			return;
		}

		int chunkHeight = chunk[0].length;
		int chunkY = 0;

		if (blockY < chunkY || blockY >= chunkY + chunkHeight) {
			return;
		}

		int blockID = chunk[blockX - chunkX][blockY - chunkY];

		if (blockID == Blocks.AIR.getId()) {
			return;
		}

		g.setColor(new Color(255, 255, 255, 50));
		g.drawRect((int) ((blockX - camera.getX()) * blockSize), (int) ((blockY - camera.getY()) * blockSize), blockSize, blockSize);
	}

	public BlockRegistry getBlockRegistry() {
		return this.blockRegistry;
	}

	public int[][][] getChunks() {
		return this.chunks;
	}

	public int getLeftChunkIndex() {
		return this.leftChunkIndex;
	}
}
