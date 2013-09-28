package ch.k42.metropolis.generator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 28.09.13
 * Time: 21:34
 * To change this template use File | Settings | File Templates.
 */
public class BedrockFloorPopulator extends BlockPopulator {
    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for(int x=0;x<16;x++){
            for(int z=0;z<16;z++){
                chunk.getBlock(x,0,z).setType(Material.BEDROCK); //TODO add some randomness to floor
            }
        }
    }
}
