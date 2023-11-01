package fr.nicolas.wispy.game.world;

import fr.nicolas.wispy.Runner;
import fr.nicolas.wispy.game.Game;
import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.registry.BlocksRegistry;
import fr.nicolas.wispy.game.craft.CraftManager;
import fr.nicolas.wispy.game.entities.Entity;
import fr.nicolas.wispy.game.entities.Player;
import fr.nicolas.wispy.game.entities.registry.EntitiesRegistry;
import fr.nicolas.wispy.game.items.ItemStack;
import fr.nicolas.wispy.game.items.registry.ItemsRegistry;
import fr.nicolas.wispy.game.render.Camera;
import fr.nicolas.wispy.game.render.Vector2D;
import fr.nicolas.wispy.game.utils.Assets;
import fr.nicolas.wispy.game.world.chunks.Chunk;
import fr.nicolas.wispy.game.world.worldgen.WorldGeneration;
import fr.nicolas.wispy.ui.renderer_screens.GameRenderer;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WorldManager {

	public static final int CHUNK_WIDTH = 82;
	public static final int CHUNK_HEIGHT = 512;
	public static final int DAY_DURATION = 20*60*1000;

	private int leftChunkIndex = -1;
	private final Chunk[] chunks;
	private final Dimensions dimension = Dimensions.OVERWORLD;

	private File worldDir;
	private final Game game;
	private final Camera camera;

	private final CraftManager craftManager;
	private final ItemsRegistry itemRegistry;
	private final BlocksRegistry blockRegistry;
	private final EntitiesRegistry entitiesRegistry;

	private final WorldGeneration worldGeneration;

	private final ArrayList<Entity> entities = new ArrayList<>();

	private JSONObject worldData = new JSONObject();

	private boolean loaded = false;
	private boolean playerSpawned = false;

	private long worldTimeReference = 0;

	private final BufferedImage[] destroyStageTextures = new BufferedImage[10];

	public WorldManager(Game game, Camera camera) {
		this.game = game;
		this.camera = camera;

		this.craftManager = new CraftManager();
		this.itemRegistry = new ItemsRegistry(craftManager);
		this.blockRegistry = new BlocksRegistry(itemRegistry);
		this.entitiesRegistry = new EntitiesRegistry(this);
		this.craftManager.loadRecipes();

		this.chunks = new Chunk[3];
		this.worldGeneration = new WorldGeneration(this);

		for (int i = 0; i < 10; i++) {
			this.destroyStageTextures[i] = Assets.get("destroy/destroy_stage_" + i);
		}
	}

	public void gameTick(double elapsedTime) {
		for (Entity entity : entities) {
			entity.tick(elapsedTime);
		}

		entities.removeIf(entity -> entity.isDead() && !(entity instanceof Player));
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void removeEntity(Entity entity) {
		this.entities.remove(entity);
	}

	public void loadWorld(String worldName) {
		this.worldDir = new File("Wispy/worlds/" + worldName);

		if (!this.worldDir.isDirectory()) {
			this.worldDir.mkdirs();
		}

		this.worldTimeReference = System.currentTimeMillis();

		File worldDataFile = new File(worldDir, "world.json");
		if (worldDataFile.isFile()) {
			try {
				this.worldData = new JSONObject(FileUtils.readFileToString(worldDataFile, "UTF-8"));

				if (this.worldData.has("time")) {
					worldTimeReference = this.worldData.getLong("time");
				}

				if (this.worldData.has("seed")) {
					worldGeneration.setSeed(this.worldData.getLong("seed"));
				}

				loadWorldData();

				this.leftChunkIndex = (int) Math.floor(game.getPlayer().getX() / CHUNK_WIDTH);

				for (int i = 0; i < this.chunks.length; i++) {
					this.chunks[i] = loadChunk(this.leftChunkIndex + i);
				}
			} catch (IOException e) {
				e.printStackTrace();
				loadWorldData();
			}
		} else {
			this.worldData = new JSONObject();
			loadWorldData();
		}

		for (int i = 0; i < this.chunks.length; i++) {
			this.chunks[i] = loadChunk(this.leftChunkIndex + i);
		}

		if (!this.playerSpawned) {
			teleportPlayerToSpawnPoint();
			this.playerSpawned = true;
		}

		this.loaded = true;
	}

	public void closeWorld() {
		if (!this.loaded) {
			return;
		}

		for (int i = 0; i < this.chunks.length; i++) {
			saveChunk(this.chunks[i], this.leftChunkIndex + i);
		}

		saveWorldData();

		File worldDataFile = new File(worldDir, "world.json");
		try {
			FileUtils.writeStringToFile(worldDataFile, this.worldData.toString(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.loaded = false;
	}

	public void saveWorldData() {
		JSONObject playerData = new JSONObject();

		playerData.put("x", this.game.getPlayer().getX());
		playerData.put("y", this.game.getPlayer().getY());
		playerData.put("dimension", 0);

		JSONObject inventory = new JSONObject();
		for (int i = 0; i < this.game.getPlayer().getInventory().getSize(); i++) {
			ItemStack itemStack = this.game.getPlayer().getInventory().getItem(i);
			if (itemStack != null) {
				JSONObject itemStackData = new JSONObject();
				itemStackData.put("id", itemStack.getItem().getId());
				itemStackData.put("amount", itemStack.getAmount());
				inventory.put("" + i, itemStackData);
			}
		}

		playerData.put("inventory", inventory);

		this.worldData.put("player", playerData);
		this.worldData.put("time", this.worldTimeReference);
		this.worldData.put("seed", this.worldGeneration.getSeed());
	}

	public void loadWorldData() {
		if (this.worldData.has("player")) {
			JSONObject playerData = this.worldData.getJSONObject("player");
			if (playerData != null && playerData.has("x") && playerData.has("y")) {
				this.game.getPlayer().setPos(playerData.getDouble("x"), playerData.getDouble("y"));
				this.playerSpawned = true;
			}

			if (playerData != null && playerData.has("inventory")) {
				JSONObject inventory = playerData.getJSONObject("inventory");
				for (int i = 0; i < this.game.getPlayer().getInventory().getSize(); i++) {
					if (inventory.has("" + i)) {
						JSONObject itemStackData = inventory.getJSONObject("" + i);
						int id = itemStackData.getInt("id");
						int amount = itemStackData.getInt("amount");
						ItemStack itemStack = new ItemStack(this.itemRegistry.getItem(id), amount);
						this.game.getPlayer().getInventory().setItem(i, itemStack);
					}
				}
			}
		}
	}

	private void teleportPlayerToSpawnPoint() {
		for (int i = 0; i < this.chunks.length; i++) {
			int chunkX = this.leftChunkIndex + i;
			Chunk chunk = this.chunks[i];

			int fromX = -1;

			if (chunk != null) {
				for (int x = 0; x < chunk.getLandLevels().length; x++) {
					if (chunk.getFluidLevels()[x] == 0 || chunk.getFluidLevels()[x] > chunk.getLandLevels()[x]) {
						if (fromX == -1) {
							fromX = x;
						}
					} else if (fromX != -1) {
						x -= (x - fromX) / 2;
						this.game.getPlayer().setPos(chunkX * chunk.getWidth() + x, chunk.getLandLevels()[x] - 1);
						return;
					}
				}
				if (fromX != -1) {
					int x = chunk.getLandLevels().length - (chunk.getLandLevels().length - fromX) / 2;
					this.game.getPlayer().setPos(chunkX * chunk.getWidth() + x, chunk.getLandLevels()[x] - 1);
					return;
				}
			}
		}
		this.game.getPlayer().setPos(0, this.chunks[1].getLandLevels()[0]);
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

		try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(new File(dimensionFolder, index + ".chunk").toPath())))) {
			dos.writeInt(chunk.getBlocks().length);
			for (Block block : chunk.getBlocks()) {
				byte[] chunkBytes = block.toBytes();
				dos.writeInt(chunkBytes.length);
				dos.write(chunkBytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(new File(dimensionFolder, index + ".entities").toPath())))) {
			List<Entity> chunkEntities = entities.stream()
					.filter(entity -> entity.getX() >= index * CHUNK_WIDTH && entity.getX() < (index + 1) * CHUNK_WIDTH)
					.collect(Collectors.toList());

			dos.writeInt(chunkEntities.size());
			for (Entity entity : chunkEntities) {
				byte[] entityBytes = entity.toBytes();
				dos.writeInt(entityBytes.length);
				dos.write(entityBytes);
			}

			entities.removeAll(chunkEntities);
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

		try (DataInputStream dis = new DataInputStream(new GZIPInputStream(Files.newInputStream(chunkFile.toPath())))) {
			int arrayLength = dis.readInt();
			for (int i = 0; i < arrayLength; i++) {
				int blockBytesLength = dis.readInt();
				byte[] blockBytes = new byte[blockBytesLength];
				dis.readFully(blockBytes);
				blocks[i] = Block.fromBytes(blockBytes);
			}
			chunk.setBlocks(blocks);
		} catch (Exception e) {
			e.printStackTrace();

			chunk = worldGeneration.generateChunk(index);
		}

		File entitiesFile = new File(dimensionFolder, index + ".entities");
		if (entitiesFile.isFile()) {
			try (DataInputStream dis = new DataInputStream(new GZIPInputStream(Files.newInputStream(entitiesFile.toPath())))) {
				int arrayLength = dis.readInt();
				for (int i = 0; i < arrayLength; i++) {
					int entityBytesLength = dis.readInt();
					byte[] entityBytes = new byte[entityBytesLength];
					dis.readFully(entityBytes);
					try {
						this.entities.add(Entity.fromBytes(entityBytes));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return chunk;
	}

	public void gameTick(long gameTick) {
		for (int i = 0; i < this.chunks.length; i++) {
			Chunk chunk = this.chunks[i];
			if (chunk != null) {
				for (int x = 0; x < chunk.getWidth(); x++) {
					for (int y = 0; y < chunk.getHeight(); y++) {
						Block block = chunk.getBlock(x, y);
						if (block.getTickPlaced() == gameTick) {
							continue;
						}
						block.tick(this, x + (i + leftChunkIndex) * chunk.getWidth(), y, gameTick);
					}
				}
			}
		}
	}

	public Block getBlock(int x, int y) {
		int index = (int) Math.floor(x / (double) CHUNK_WIDTH);
		if (index < this.leftChunkIndex || index >= this.leftChunkIndex + this.chunks.length) {
			return Blocks.AIR.getBlock();
		}
		return this.chunks[index - this.leftChunkIndex].getBlock(x - index * CHUNK_WIDTH, y);
	}

	public void setBlock(int x, int y, Block block) {
		int index = (int) Math.floor(x / (double) CHUNK_WIDTH);
		if (index < this.leftChunkIndex || index >= this.leftChunkIndex + this.chunks.length) {
			return;
		}
		this.chunks[index - this.leftChunkIndex].setBlock(x - index * CHUNK_WIDTH, y, block);
	}

	public void render(Graphics2D g, double width, double height, boolean liquidLayer) {
		for (int i = 0; i < this.chunks.length; i++) {
			renderChunk(g, this.chunks[i], this.leftChunkIndex + i, width, height, liquidLayer);
		}

		if (!liquidLayer) {
			for (Entity entity : entities) {
				entity.render(g);
			}
		}
	}

	private void renderChunk(Graphics2D g, Chunk chunk, int chunkIndex, double width, double height, boolean liquidLayer) {
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

		int[] landLevels = chunk.getLandLevels();
		int[] fluidLevels = chunk.getFluidLevels();
		float timeFactor = getTimeFactor();

		for (int x = startingPointX; x < Math.min(camera.getBlockX() + 1 + width - chunkX, chunkWidth); x++) {
			int blockX = x + chunkX;

			for (int y = startingPointY; y < Math.min(camera.getBlockY() + 1 + height - chunkY, chunkHeight); y++) {
				Block block = chunk.getBlock(x, y);

				if (liquidLayer && !block.isLiquid()) {
					continue;
				}

				boolean wasLiquid = false;

				Block decorationBlock = block.getDecorationType().getBlock();
				Block originalBlock = block.getOriginalType().getBlock();
				if (block.isLiquid() && !liquidLayer) {
					if (decorationBlock.getType() != Blocks.AIR) {
						block = decorationBlock;
						decorationBlock = originalBlock;
						originalBlock = null;
						wasLiquid = true;
					} else if (originalBlock.getType() != Blocks.AIR) {
						block = originalBlock;
						decorationBlock = null;
						originalBlock = null;
						wasLiquid = true;
					}
				}

				float darknessOpacity = 0;

				if (y >= landLevels[x]) {
					float minOpacity = 0;
					if (y >= fluidLevels[x] && fluidLevels[x] != 0) {
						minOpacity = Math.max(0, Math.min(255, (y - fluidLevels[x]) / 24.0F));
					}

					darknessOpacity = (y - landLevels[x]) / 10.0F;
					darknessOpacity = Math.max(minOpacity, Math.min(1.0F, darknessOpacity));
				} else if (block.isLiquid() && y >= fluidLevels[x] && liquidLayer) {
					darknessOpacity = (y - fluidLevels[x]) / 24.0F;
				}

				double dx = blockX - game.getPlayer().getX();
				double dy = y - game.getPlayer().getY();
				double distance = Math.sqrt(dx * dx + dy * dy);
				double circleRadius = 10.0;

				if (distance < circleRadius) {
					float distanceFactor = 1 - (float) ((circleRadius - distance) / circleRadius);
					darknessOpacity = Math.min(darknessOpacity, distanceFactor * distanceFactor);
				}

				darknessOpacity += 0.6F * timeFactor;

				if (block.isBackgroundBlock() && !block.isTransparent()) {
					darknessOpacity += 0.3F;
				}

				if (liquidLayer || block.renderAsSolidColor()) {
					int color = block.getSolidColor();

					int red = (color >> 16) & 0xFF;
					int green = (color >> 8) & 0xFF;
					int blue = color & 0xFF;

					int blendedRed = Math.max(0, Math.min((int) (red * (1 - darknessOpacity)), 255));
					int blendedGreen = Math.max(0, Math.min((int) (green * (1 - darknessOpacity)), 255));
					int blendedBlue = Math.max(0, Math.min((int) (blue * (1 - darknessOpacity)), 255));

					g.setColor(new Color(blendedRed, blendedGreen, blendedBlue, 175));
					g.fillRect(blockX, y, 1, 1);
				} else {
					if (darknessOpacity < 1.0F && block.getType() != Blocks.AIR) {
						if (block.isTransparent()) {
							float darkness = darknessOpacity;
							Block background = null;

							if (decorationBlock != null && !decorationBlock.isTransparent()) {
								background = decorationBlock;
							} else if (originalBlock != null && !originalBlock.isTransparent()) {
								background = originalBlock;
							}

							if (background != null) {
								darkness += 0.15F;

								if (darkness < 1.0F) {
									g.drawImage(background.getTexture(), blockX, y, 1, 1, null);
								}

								if (!wasLiquid) {
									g.setColor(new Color(0, 0, 0, Math.min(1.0F, darkness)));
									g.fillRect(blockX, y, 1, 1);
								}
							} else {
								g.setColor(game.getGameRenderer().getSkyColor());
								g.fillRect(blockX, y, 1, 1);
							}
						}

						g.drawImage(block.getTexture(), blockX, y, 1, 1, null);
					}

					g.setColor(new Color(0, 0, 0, Math.min(1.0F, darknessOpacity)));
					g.fillRect(blockX, y, 1, 1);
				}
			}
		}
	}

	public void renderSelection(Graphics2D g, int blockSize, Vector2D mouseLocation) {
		int blockX = (int) Math.floor(camera.getX() + mouseLocation.x / (double) blockSize);
		int blockY = (int) Math.floor(camera.getY() + mouseLocation.y / (double) blockSize);

		if (Math.abs(blockX - game.getPlayer().getX()) > 10 || Math.abs(blockY - game.getPlayer().getY()) > 10) {
			game.setSelectedBlock(null, 0, 0);
			return;
		}

		Block block = getBlock(blockX, blockY);
		Block leftBlock = getBlock(blockX - 1, blockY);
		Block rightBlock = getBlock(blockX + 1, blockY);
		Block topBlock = getBlock(blockX, blockY - 1);
		Block bottomBlock = getBlock(blockX, blockY + 1);

		boolean canBreak = block.canBreak() && (!leftBlock.takeBreakPriority() || !rightBlock.takeBreakPriority() || !topBlock.takeBreakPriority() || !bottomBlock.takeBreakPriority());
		boolean canReplace = block.canReplace() && (!leftBlock.takeReplacePriority() || !rightBlock.takeReplacePriority() || !topBlock.takeReplacePriority() || !bottomBlock.takeReplacePriority());

		if (!canBreak && !canReplace) {
			game.setSelectedBlock(null, 0, 0);
			return;
		}

		if (game.getSelectedBlock() != block) {
			game.setSelectedBlock(block, blockX, blockY);
		}

		if (canBreak && !canReplace) {
			g.setColor(new Color(150, 64, 7, 150));
		} else if (!canBreak) {
			g.setColor(new Color(7, 64, 150, 150));
		} else {
			g.setColor(new Color(150, 7, 150, 150));
		}
		g.setStroke(new BasicStroke(3.0F / blockSize));
		g.drawRect(blockX, blockY, 1, 1);

		if (game.getBlockBreakStartTime() > 0) {
			int destroyStage = (int) Math.floor((System.currentTimeMillis() - game.getBlockBreakStartTime()) / 500.0 * 10);
			if (destroyStage >= destroyStageTextures.length) {
				game.getSelectedBlock().onBreak(this, blockX, blockY);
				game.setSelectedBlock(null, 0, 0);
				setBlock(blockX, blockY, Blocks.AIR.getBlock());
			} else {
				g.drawImage(destroyStageTextures[destroyStage], blockX, blockY, 1, 1, null);
			}
		}
	}

	public CraftManager getCraftManager() {
		return this.craftManager;
	}

	public BlocksRegistry getBlockRegistry() {
		return this.blockRegistry;
	}

	public ItemsRegistry getItemRegistry() {
		return this.itemRegistry;
	}

	public EntitiesRegistry getEntitiesRegistry() {
		return this.entitiesRegistry;
	}

	public Chunk[] getChunks() {
		return this.chunks;
	}

	public int getLeftChunkIndex() {
		return this.leftChunkIndex;
	}

	public long getTime() {
		return (System.currentTimeMillis() - this.worldTimeReference + DAY_DURATION / 2 + DAY_DURATION / 4) % DAY_DURATION;
	}

	public float getTimeFactor() {
		float sineValue = (float) Math.sin((getTime() / (float) DAY_DURATION) * 2 * Math.PI);
		return (sineValue + 1) / 2;
	}
}
