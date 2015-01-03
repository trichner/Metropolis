package ch.k42.metropolis.generator.populators;

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
       chunk.getBlock(0,0,0).setType(Material.DIAMOND);
    }
}