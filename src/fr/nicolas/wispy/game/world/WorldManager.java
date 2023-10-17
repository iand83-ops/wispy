package fr.nicolas.wispy.game.world;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.BlockRegistry;
import fr.nicolas.wispy.game.blocks.Blocks;
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

	private String worldName;
	private File worldDir;
	private final Player player;

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

	public WorldManager(Player player) {
		this.player = player;
		this.blockRegistry = new BlockRegistry();
	}

	public void loadWorld(String worldName) {
		this.worldName = worldName;

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
		this.player.x = 0;
		this.player.y = getPlayerSpawnY();
	}

	public int getPlayerSpawnY() {
		// TODO: Système à refaire
		int y = 0;
		while (mapBCenter[0][y] == Blocks.AIR.getId()) {
			y++;
		}
		return y;
	}

	public void newLoadingMapThread(Runner runner, GamePanel gamePanel) {
		Thread loadNextMap = new Thread(() -> {
            while (true) {
                int newX;
                if (mapBRight != null) {
                    newX = ((mapBRight.length / 2 + mapBRightNum * mapBRight.length) * gamePanel.getNewBlockWidth())
                            - (int) (player.getX() * gamePanel.getNewBlockWidth() / GamePanel.BLOCK_SIZE);
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
                    newX = ((mapBLeft.length / 2 + mapBLeftNum * mapBLeft.length) * gamePanel.getNewBlockWidth())
                            - (int) (player.getX() * gamePanel.getNewBlockWidth() / GamePanel.BLOCK_SIZE);
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

	public void computeCollisions(int width, int height,
								  int newBlockWidth, int newBlockHeight, int playerX, int playerY, int playerWidth, int playerHeight,
								  GamePanel gamePanel) {
		computeCollision(mapBCenter, mapBCenterNum, width, height, newBlockWidth, newBlockHeight,
				gamePanel, playerWidth, playerHeight, playerX, playerY);
		computeCollision(mapBLeft, mapBLeftNum, width, height, newBlockWidth, newBlockHeight,
				gamePanel, playerWidth, playerHeight, playerX, playerY);
		computeCollision(mapBRight, mapBRightNum, width, height, newBlockWidth, newBlockHeight,
				gamePanel, playerWidth, playerHeight, playerX, playerY);

		hasFoundFallingCollision = false;
		hasFoundUpCollision = false;
		hasFoundRightCollision = false;
		hasFoundLeftCollision = false;
	}

	public void drawMaps(Graphics g, int width, int height, int newBlockWidth, int newBlockHeight) {
		drawMap(g, mapBCenter, mapBCenterNum, width, height, newBlockWidth, newBlockHeight);
		drawMap(g, mapBLeft, mapBLeftNum, width, height, newBlockWidth, newBlockHeight);
		drawMap(g, mapBRight, mapBRightNum, width, height, newBlockWidth, newBlockHeight);
	}

	public void drawSelections(Graphics g, int width, int height, int newBlockWidth, int newBlockHeight, Point mouseLocation) {
		drawSelection(g, mapBCenter, mapBCenterNum, width, height, newBlockWidth, newBlockHeight, mouseLocation);
		drawSelection(g, mapBLeft, mapBLeftNum, width, height, newBlockWidth, newBlockHeight, mouseLocation);
		drawSelection(g, mapBRight, mapBRightNum, width, height, newBlockWidth, newBlockHeight, mouseLocation);
	}

	// TODO: Fonction à réorganiser
	private void computeCollision(int[][] mapB, int times, int width,
								  int height, int newBlockWidth, int newBlockHeight, GamePanel gamePanel, int playerWidth, int playerHeight,
								  int playerX, int playerY) {

		if (mapB != null) {
			for (int x = 0; x < mapB.length; x++) {
				int newX = ((x + times * mapB.length) * newBlockWidth) - (int) (player.getX() / GamePanel.BLOCK_SIZE * newBlockWidth);
				if (newX >= -350 && newX <= width + 350) {
					for (int y = 0; y < mapB[0].length; y++) {
						int newY = (y * newBlockHeight) - (int) (player.getY() / GamePanel.BLOCK_SIZE * newBlockHeight);
						if (newY >= -350 && newY <= height + 350) {
							if (mapB[x][y] != Blocks.AIR.getId()) {
								// Test des collisions avec le joueur
								if (!hasFoundFallingCollision) {
									if (new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
											.contains(new Point(playerX, playerY + playerHeight))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight).contains(
													new Point(playerX + playerWidth - 1, playerY + playerHeight))) {

										gamePanel.getPlayer().setFalling(false);
										hasFoundFallingCollision = true;

									} else {
										gamePanel.getPlayer().setFalling(true);
									}
								}
								if (!hasFoundUpCollision) {
									if (new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
											.contains(new Point(playerX, playerY - 1))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
													.contains(new Point(playerX + playerWidth - 1, playerY - 1))) {

										gamePanel.getPlayer().setCanGoUp(false);
										hasFoundUpCollision = true;

									} else {
										gamePanel.getPlayer().setCanGoUp(true);
									}
								}
								if (!hasFoundRightCollision) {
									if (new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
											.contains(new Point(playerX + playerWidth, playerY))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight).contains(
													new Point(playerX + playerWidth, playerY + playerHeight - 1))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight).contains(
													new Point(playerX + playerWidth, playerY + playerHeight / 2))) {

										gamePanel.getPlayer().setCanGoRight(false);
										hasFoundRightCollision = true;

									} else {
										gamePanel.getPlayer().setCanGoRight(true);
									}
								}
								if (!hasFoundLeftCollision) {
									if (new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
											.contains(new Point(playerX - 1, playerY))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
													.contains(new Point(playerX - 1, playerY + playerHeight - 1))
											|| new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
													.contains(new Point(playerX - 1, playerY + playerHeight / 2))) {

										gamePanel.getPlayer().setCanGoLeft(false);
										hasFoundLeftCollision = true;

									} else {
										gamePanel.getPlayer().setCanGoLeft(true);
									}
								}
							}
						} else if (newY > 0) {
							break;
						}
					}
				} else if (newX > 0) {
					break;
				}
			}
		}
	}

	private void drawMap(Graphics g, int[][] mapB, int times, int width, int height, int newBlockWidth, int newBlockHeight) {
		if (mapB != null) {
			for (int x = 0; x < mapB.length; x++) {
				int newX = ((x + times * mapB.length) * newBlockWidth) - (int) (player.getX() / GamePanel.BLOCK_SIZE * newBlockWidth);
				if (newX >= -350 && newX <= width + 350) {
					for (int y = 0; y < mapB[0].length; y++) {
						int newY = (y * newBlockHeight) - (int) (player.getY() / GamePanel.BLOCK_SIZE * newBlockHeight);
						if (newY >= -350 && newY <= height + 350) {
							if (mapB[x][y] != Blocks.AIR.getId()) {
								Block block = this.blockRegistry.getBlock(mapB[x][y]);
								g.drawImage(block.getTexture(), newX, newY, newBlockWidth, newBlockHeight, null);
							}
						} else if (newY > 0) {
							break;
						}
					}
				} else if (newX > 0) {
					break;
				}
			}
		}
	}

	public void drawSelection(Graphics g, int[][] mapB, int times, int width,
							  int height, int newBlockWidth, int newBlockHeight, Point mouseLocation) {
		if (mapB != null) {
			for (int x = 0; x < mapB.length; x++) {
				int newX = ((x + times * mapB.length) * newBlockWidth) - (int) (player.getX() / GamePanel.BLOCK_SIZE * newBlockWidth);
				if (newX >= -350 && newX <= width + 350) {
					for (int y = 0; y < mapB[0].length; y++) {
						int newY = (y * newBlockHeight) - (int) (player.getY() / GamePanel.BLOCK_SIZE * newBlockHeight);
						if (newY >= -350 && newY <= height + 350) {
							if (mapB[x][y] != Blocks.AIR.getId()) {
								// Block selection
								if (new Rectangle(newX, newY, newBlockWidth, newBlockHeight)
										.contains(mouseLocation)) {
									g.setColor(new Color(255, 255, 255, 50));
									g.drawRect(newX, newY, newBlockWidth, newBlockHeight);
								}
							}
						} else if (newY > 0) {
							break;
						}
					}
				} else if (newX > 0) {
					break;
				}
			}
		}
	}

}
