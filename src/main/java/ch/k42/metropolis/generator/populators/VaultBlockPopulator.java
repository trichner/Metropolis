package ch.k42.metropolis.generator.populators;

import ch.k42.metropolis.minions.Minions;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public class VaultBlockPopulator extends BlockPopulator {

    @Override
    public void populate(World aWorld, Random random, Chunk chunk) { // we should make sure that the whol
        Minions.i("Placing diamond!");
       chunk.getBlock(0,10,0).setType(Material.DIAMOND_BLOCK);

        if(chunk.getX() == 0 && chunk.getZ() == 0){
            //place a fat cuboid
        }
    }
}