package fr.nicolas.wispy.game.world.worldgen;

import fr.nicolas.wispy.game.blocks.Block;
import fr.nicolas.wispy.game.blocks.registry.Blocks;
import fr.nicolas.wispy.game.blocks.types.OreBlock;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.game.world.chunks.Chunk;
import fr.nicolas.wispy.game.world.decorations.*;
import org.spongepowered.noise.LatticeOrientation;
import org.spongepowered.noise.Noise;
import org.spongepowered.noise.NoiseQualitySimplex;

import java.util.Arrays;
import java.util.Random;

public class WorldGeneration {

    private final WorldManager worldManager;
    private long seed = new Random().nextLong();

    private final Decoration[] decorations = {
            new TreeDecoration(),
            new OreDecoration(),
            new TallGrassDecoration(),
            new CaveVinesDecoration()
    };

    public WorldGeneration(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public Chunk generateChunk(int index) {
        Random random = new Random(seed);

        int chunkWidth = WorldManager.CHUNK_WIDTH;
        int chunkHeight = WorldManager.CHUNK_HEIGHT;

        Chunk chunk = new Chunk(worldManager);
        Block[] blocks = new Block[chunkWidth * chunkHeight];
        Arrays.fill(blocks, Blocks.AIR.getBlock());
        chunk.setBlocks(blocks);

        int waterLevel = 60;

        // Fill with water
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                if (y >= waterLevel) {
                    chunk.setBlock(x, y, Blocks.WATER.getBlock());
                }
            }
        }

        int worldX = index * chunkWidth;
        int landHeight = waterLevel;

        // Terrain shape
        for (int x = 0; x < chunkWidth; x++) {
            int yLevel = (int) (waterLevel + Noise.simplexStyleGradientCoherentNoise3D((worldX + x) * 0.01, 0, 0, 0, LatticeOrientation.CLASSIC, NoiseQualitySimplex.SMOOTH) * landHeight - landHeight / 2.75);
            for (int y = 0; y < chunkHeight; y++) {
                if (y >= yLevel) {
                    Block block = Blocks.STONE.getBlock();
                    chunk.setBlock(x, y, block);
                    block.setDecorationType(Blocks.STONE);
                    block.setOriginalType(Blocks.STONE);
                }
            }
        }

        // Bedrock
        for (int x = 0; x < chunkWidth; x++) {
            for (int i = 0; i < 15; i++) {
                chunk.setBlock(x, chunkHeight - i - 1, Blocks.BEDROCK.getBlock());
            }
        }

        // Add grass
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                if (chunk.getBlock(x, y).getType() == Blocks.STONE && chunk.getBlock(x, y - 1).getType() == Blocks.AIR) {
                    chunk.overwriteBlock(x, y, Blocks.GRASS.getBlock());
                }
            }
        }

        // Add sand
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                if (chunk.getBlock(x, y).getType() == Blocks.STONE && chunk.getBlock(x, y - 1).getType() == Blocks.WATER) {
                    chunk.overwriteBlock(x, y, Blocks.SAND.getBlock());
                }
            }
        }

        // Add dirt or extend sand
        for (int x = 0; x < chunkWidth; x++) {
            int layers = (int) (4 + Noise.simplexStyleGradientCoherentNoise3D((worldX + x) * 0.01, 0, 0, 0, LatticeOrientation.CLASSIC, NoiseQualitySimplex.SMOOTH) * 4);
            for (int y = 0; y < chunkHeight; y++) {
                Block block = chunk.getBlock(x, y);
                Block blockToPlace;

                if (block.getType() == Blocks.GRASS) {
                    blockToPlace = Blocks.DIRT.getBlock();
                } else if (block.getType() == Blocks.SAND) {
                    blockToPlace = Blocks.SAND.getBlock();
                } else {
                    continue;
                }

                y++;

                for (int i = 0; i < layers; i++) {
                    if (y + i < chunkHeight) {
                        chunk.overwriteBlock(x, y + i, blockToPlace.copy());
                    }
                }

                // Gradient
                for (int gradientX = -1; gradientX < 2; gradientX++) {
                    for (int gradientY = 0; gradientY < layers + 3; gradientY++) {
                        int blockX = x + gradientX;
                        int blockY = y + gradientY;
                        Block currentBlock = chunk.getBlock(blockX, blockY);
                        if (random.nextBoolean() && (currentBlock.getType() == Blocks.STONE || currentBlock.getType() == Blocks.SAND || currentBlock.getType() == Blocks.DIRT)) {
                            chunk.overwriteBlock(blockX, blockY, blockToPlace.copy());
                        }
                    }
                }
                break;
            }
        }

        // Caves
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                Block block = chunk.getBlock(x, y);

                if (block.getType() == Blocks.STONE || block.getType() == Blocks.SAND || block instanceof OreBlock) {
                    double noise = Noise.simplexStyleGradientCoherentNoise3D((worldX + x) * 0.08, y * 0.08, 0, 0, LatticeOrientation.CLASSIC, NoiseQualitySimplex.SMOOTH);
                    if (noise > 0.5) {
                        block.setBackgroundBlock(true);
                    }
                }
            }
        }

        // Add decorations
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {

                int priority = 0;
                int[][] decorationBlocksToPlace = null;
                Decoration decorationToPlace = null;
                for (Decoration decoration : decorations) {
                    if (decoration.testBase(chunk, x, y) && decoration.getPriority() > priority && random.nextDouble() <= decoration.getChance()) {
                        int[][] decorationBlocks = decoration.getBlocks(random);
                        // Must be included in the chunk
                        if (x - decorationBlocks.length / 2 >= 0 && x + decorationBlocks.length / 2 < chunkWidth && y - decorationBlocks[0].length >= 0) {
                            priority = decoration.getPriority();
                            decorationBlocksToPlace = decorationBlocks;
                            decorationToPlace = decoration;
                        }
                    }
                }

                if (decorationBlocksToPlace != null) {
                    for (int decorationX = 0; decorationX < decorationBlocksToPlace.length; decorationX++) {
                        for (int decorationY = 0; decorationY < decorationBlocksToPlace[decorationX].length; decorationY++) {
                            int blockX = x + decorationX - (decorationBlocksToPlace.length / 2);
                            int blockY = y + (decorationToPlace.isGoingDown() ? decorationY : -decorationY);
                            if (blockX >= 0 && blockX < chunkWidth && blockY >= 0) {
                                int id = decorationBlocksToPlace[decorationX][decorationY];
                                if (id != 0 && decorationToPlace.testSpace(chunk, blockX, blockY)) {
                                    Block block = worldManager.getBlockRegistry().getBlock(id);
                                    Block initialBlock = chunk.getBlock(blockX, blockY);

                                    chunk.setBlock(blockX, blockY, block);
                                    if (decorationToPlace.areBackgroundBlocks() || initialBlock.isBackgroundBlock()) {
                                        block.setBackgroundBlock(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return chunk;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return this.seed;
    }
}