package ch.k42.metropolis.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Fills up the bottom layer with bedrock, so nobody reaches the void
 * @author Thomas Richner
 */
public class BedrockFloorPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for(int x=0;x<16;x++){
            for(int z=0;z<16;z++){
                chunk.getBlock(x,0,z).setType(Material.BEDROCK);
                for(int bed = random.nextInt(3)+1; bed>0; bed--){
                    chunk.getBlock(x,bed,z).setType(Material.BEDROCK);
                }
            }
        }
    }
}
