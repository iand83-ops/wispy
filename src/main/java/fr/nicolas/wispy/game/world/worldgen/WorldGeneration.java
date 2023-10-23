package fr.nicolas.wispy.game.world.worldgen;

import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.game.world.chunks.Chunk;

public class WorldGeneration {

    private final WorldManager worldManager;

    public WorldGeneration(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public Chunk generateChunk(int index) {
        Chunk chunk = new Chunk(worldManager);
        int[][] blocks = new int[WorldManager.CHUNK_WIDTH][WorldManager.CHUNK_HEIGHT];

        int worldX = (index + worldManager.getLeftChunkIndex()) * WorldManager.CHUNK_WIDTH;
        int worldY = 0;

        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[x].length; y++) {
//                double value = PerlinNoise.noise((worldX + x) * 0.1, (worldY + y) * 0.1);
//                blocks[x][y] = determineBlockType(value);
            }
        }

        chunk.setBlocks(blocks);
        return chunk;
    }

    private int determineBlockType(double noiseValue) {
        if (noiseValue < 0.3) {
            return 0; // Example: Air
        } else if (noiseValue < 0.7) {
            return 1; // Example: Dirt
        } else {
            return 2; // Example: Stone
        }
    }
}