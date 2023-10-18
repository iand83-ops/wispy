package fr.nicolas.wispy.game.world;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BlockRegistry;
import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.render.AABB;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.panels.GamePanel;
import fr.nicolas.wispy.panels.components.game.Player;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class WorldManager {

	private int mapBLeftNum = -1;
	private int mapBCenterNum = 0;
	private int mapBRightNum = 1;

	public int[][] mapBLeft;
	public int[][] mapBRight;
	public int[][] mapBCenter;

	private File worldDir;
	private final Player player;
	private final Camera camera;

	// Random generation variables
	private int state = 0;
	private int changeStateNum = 5;
	private int currentNum = 0;
	private int lastY = 10;
	private int lastState = 0;

	private boolean hasFoundFallingCollision = false;
	private boolean hasFoundUpCollision = false;
	private boolean hasFoundRightCollision = false;
	private boolean hasFoundLeftCollision = false;

	private BlockRegistry blockRegistry;

	public WorldManager(Player player, Camera camera) {
		this.player = player;
		this.camera = camera;
		this.blockRegistry = new BlockRegistry();
	}

	public void loadWorld(String worldName) {
		this.worldDir = new File("Wispy/worlds/" + worldName);

		if (!this.worldDir.isDirectory()) {
			this.worldDir.mkdirs();
		}

		this.mapBLeftNum = -1;
		this.mapBLeft = loadChunk(this.mapBLeftNum);

		this.mapBCenterNum = 0;
		this.mapBCenter = loadChunk(this.mapBCenterNum);

		this.mapBRightNum = 1;
		this.mapBRight = loadChunk(this.mapBRightNum);

		// Player spawnpoint
		this.player.setPos(0, getPlayerSpawnY());
	}

	public double getPlayerSpawnY() {
		int y = 0;
		while (mapBCenter[0][y] == Blocks.AIR.getId()) {
			y++;
		}
		return y;
	}

	public void startLoadingChunkThread(Runner runner, GamePanel gamePanel) {
		Thread loadNextMap = new Thread(() -> {
            while (true) {
                int newX;
                if (mapBRight != null) {
                    newX = ((mapBRight.length / 2 + mapBRightNum * mapBRight.length) * gamePanel.getBlockSize())
                            - (int) (player.getX() * gamePanel.getBlockSize());
                    if (newX >= 0 && newX <= gamePanel.getWidth()) {
                        saveChunk(mapBLeft, mapBLeftNum);

                        mapBLeft = mapBCenter;
                        mapBLeftNum = mapBCenterNum;
                        mapBCenter = mapBRight;
                        mapBCenterNum = mapBRightNum;

						mapBRightNum++;
						mapBRight = loadChunk(mapBRightNum);
                    }
                }
                if (mapBLeft != null) {
                    newX = ((mapBLeft.length / 2 + mapBLeftNum * mapBLeft.length) * gamePanel.getBlockSize())
                            - (int) (player.getX() * gamePanel.getBlockSize());
                    if (newX >= 0 && newX <= gamePanel.getWidth()) {
                        saveChunk(mapBRight, mapBRightNum);

                        mapBRight = mapBCenter;
                        mapBRightNum = mapBCenterNum;
                        mapBCenter = mapBLeft;
                        mapBCenterNum = mapBLeftNum;

						mapBLeftNum--;
						mapBLeft = loadChunk(mapBLeftNum);
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

	public void computeCollisions(Player player) {
		player.setFalling(true);

		computeCollision(mapBCenter, mapBCenterNum, player);
		computeCollision(mapBLeft, mapBLeftNum, player);
		computeCollision(mapBRight, mapBRightNum, player);

		hasFoundFallingCollision = false;
		hasFoundUpCollision = false;
		hasFoundRightCollision = false;
		hasFoundLeftCollision = false;
	}

	public void renderChunks(Graphics g, int width, int height, int blockWidth) {
		renderChunk(g, mapBCenter, mapBCenterNum, width, height, blockWidth);
		renderChunk(g, mapBLeft, mapBLeftNum, width, height, blockWidth);
		renderChunk(g, mapBRight, mapBRightNum, width, height, blockWidth);
	}

	public void renderSelections(Graphics g, int width, Point mouseLocation) {
		renderSelection(g, mapBCenter, mapBCenterNum, width, mouseLocation);
		renderSelection(g, mapBLeft, mapBLeftNum, width, mouseLocation);
		renderSelection(g, mapBRight, mapBRightNum, width, mouseLocation);
	}

	private void computeCollision(int[][] chunk, int chunkIndex, Player player) {
		if (chunk == null) {
			return;
		}

		int chunkWidth = chunk.length;
		int chunkX = chunkWidth * chunkIndex;

        double playerX = player.getX();
        double playerY = player.getY();
        double playerWidth = player.getWidth();
        double playerHeight = player.getHeight();

		if (playerX + playerWidth < chunkX || playerX > chunkX + chunkWidth) {
			return;
		}

		int chunkY = 0;
		int chunkHeight = chunk[0].length;

		if (playerY + playerHeight < 0 || playerY > chunkHeight) {
			player.setFalling(true);
			return;
		}

        double playerChunkX = playerX - chunkX;
        double playerChunkY = playerY - chunkY;

		float offset = 1.0F / GamePanel.BLOCK_RESOLUTION;

		AABB playerLeftAABB = new AABB(new Vector2D(playerChunkX, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX, playerChunkY - offset));
		AABB playerRightAABB = new AABB(new Vector2D(playerChunkX + playerWidth, playerChunkY - playerHeight + offset), new Vector2D(playerChunkX + playerWidth, playerChunkY - offset));
		AABB playerUpAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY - playerHeight), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY - playerHeight));
		AABB playerDownAABB = new AABB(new Vector2D(playerChunkX + offset, playerChunkY), new Vector2D(playerChunkX + playerWidth - offset, playerChunkY));

		for (int x = (int) -playerWidth; x <= playerWidth; x++) {
			for (int y = (int) -playerHeight; y <= playerHeight; y++) {
				int blockX = (int) (playerChunkX + x);
				int blockY = (int) (playerChunkY + y);

				if (blockX < 0 || blockX >= chunkWidth || blockY < 0 || blockY >= chunkHeight) {
					continue;
				}

				int blockID = chunk[blockX][blockY];

				Block block = this.blockRegistry.getBlock(chunk[blockX][blockY]);

                double blockWidth = block.getWidth();
                double blockHeight = block.getHeight();

				AABB blockAABB = new AABB(new Vector2D(blockX, blockY), new Vector2D(blockX + blockWidth, blockY + blockHeight));

				if (blockID == Blocks.AIR.getId()) {
					continue;
				}

				if (!hasFoundFallingCollision) {
					if (blockAABB.intersects(playerDownAABB)) {
						player.setFalling(false);
						hasFoundFallingCollision = true;
					} else {
						player.setFalling(true);
					}
				}
				if (!hasFoundUpCollision) {
					if (blockAABB.intersects(playerUpAABB)) {
						player.setCanGoUp(false);
						hasFoundUpCollision = true;
					} else {
						player.setCanGoUp(true);
					}
				}
				if (!hasFoundRightCollision) {
					if (blockAABB.intersects(playerRightAABB)) {
						player.setCanGoRight(false);
						hasFoundRightCollision = true;
					} else {
						player.setCanGoRight(true);
					}
				}
				if (!hasFoundLeftCollision) {
					if (blockAABB.intersects(playerLeftAABB)) {
						player.setCanGoLeft(false);
						hasFoundLeftCollision = true;
					} else {
						player.setCanGoLeft(true);
					}
				}
			}
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

}
