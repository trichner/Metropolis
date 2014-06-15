package ch.k42.metropolis.generator.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * Fills up the bottom layer with bedrock, so nobody reaches the void
 *
 * @author Thomas Richner
 */
public class BedrockFloorPopulator extends BlockPopulator {

    private static final int[][] easteregg1 = { {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  1
                                                {0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0}, //  2
                                                {0,0,1,0,0,1,0,1,0,1,0,1,1,0,1,1}, //  3
                                                {0,1,0,1,0,1,0,1,1,1,0,1,0,1,0,1}, //  4
                                                {0,1,1,1,0,1,0,1,0,1,0,1,0,0,0,1}, //  5
                                                {0,1,0,1,0,1,0,1,0,1,0,1,0,0,0,1}, //  6
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  7
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  8
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  9
                                                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0}, // 10
                                                {0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0}, // 11
                                                {0,1,0,0,0,1,1,0,0,0,1,0,0,1,1,0}, // 12
                                                {0,0,1,0,0,1,0,1,0,1,0,1,0,1,0,0}, // 13
                                                {0,0,0,1,0,1,1,0,0,1,1,1,0,1,0,0}, // 14
                                                {0,1,0,1,0,1,0,0,0,1,0,1,0,1,1,0}, // 15
                                                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0}};// 16
    private static final int[][] easteregg2 = { {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  1
                                                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, //  2
                                                {0,0,1,0,0,1,0,1,0,0,0,1,1,0,0,0}, //  3
                                                {0,1,0,1,0,1,0,0,0,0,0,1,0,1,0,0}, //  4
                                                {0,1,0,1,0,0,1,0,0,0,0,1,1,0,0,0}, //  5
                                                {0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0}, //  6
                                                {0,0,0,0,0,1,0,1,0,0,0,1,0,1,0,0}, //  7
                                                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, //  8
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, //  9
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}, // 10
                                                {1,1,0,0,0,0,0,0,0,1,1,0,0,1,0,1}, // 11
                                                {1,0,0,1,1,0,0,1,0,1,0,1,0,1,0,0}, // 12
                                                {1,1,0,1,0,1,0,1,0,1,1,0,0,0,1,0}, // 13
                                                {1,0,0,1,1,0,0,1,0,1,0,1,0,0,0,1}, // 14
                                                {1,1,0,1,0,1,0,1,0,1,1,0,0,1,0,1}, // 15
                                                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}};// 16
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if(chunk.getX()%128==0 && chunk.getZ()%128==0){
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.getBlock(x, 0, z).setType(Material.BEDROCK);
                    if(easteregg1[z][x]!=0){
                        chunk.getBlock(x, 1, z).setType(Material.BEDROCK);
                    }
                }
            }
        }else if(chunk.getX()%128==1 && chunk.getZ()%128==0){
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.getBlock(x, 0, z).setType(Material.BEDROCK);
                    if(easteregg2[z][x]!=0){
                        chunk.getBlock(x, 1, z).setType(Material.BEDROCK);
                    }
                }
            }
        }else {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.getBlock(x, 0, z).setType(Material.BEDROCK);
                    for (int bed = random.nextInt(3) + 1; bed > 0; bed--) {
                        chunk.getBlock(x, bed, z).setType(Material.BEDROCK);
                    }
                }
            }
        }
    }
}
