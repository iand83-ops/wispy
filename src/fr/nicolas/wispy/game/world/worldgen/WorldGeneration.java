package fr.nicolas.wispy.game.world.worldgen;

import fr.nicolas.wispy.game.blocks.Blocks;
import fr.nicolas.wispy.game.world.WorldManager;
import fr.nicolas.wispy.game.world.chunks.Chunk;

public class WorldGeneration {

    private final WorldManager worldManager;

    private int state = 0;
    private int changeStateNum = 5;
    private int currentNum = 0;
    private int lastY = 10;
    private int lastState = 0;

    public WorldGeneration(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public Chunk generateChunk(int index) {
        Chunk chunk = new Chunk(worldManager);
        int[][] blocks = new int[WorldManager.CHUNK_WIDTH][WorldManager.CHUNK_HEIGHT];

        // fill with water at level 10
        for (int x = 0; x < blocks.length; x++) {
            for (int y = 0; y < blocks[0].length; y++) {
                if (y > 10) {
                    blocks[x][y] = Blocks.WATER.getId();
                }
            }
        }

        int newY = 0;

        for (int x = 0; x < blocks.length; x++) {
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
            blocks[x][newY] = Blocks.GRASS.getId();

            for (int y = newY + 1; y < blocks[0].length; y++) {
                if (y < newY + 3) {
                    blocks[x][y] = Blocks.DIRT.getId();
                } else {
                    blocks[x][y] = Blocks.STONE.getId();
                }
            }
        }

        chunk.setBlocks(blocks);

        return chunk;
    }

    private int random(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }

}
