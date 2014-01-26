package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Constants;
import ch.k42.metropolis.minions.VoronoiGenerator;
import ch.k42.metropolis.minions.XYZ;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Natural decay checker
 *
 * @author Spaceribs
 */
public abstract class EnvironmentProvider {

    private Long seed;

    public EnvironmentProvider(Long newSeed) {
        this.seed = newSeed;
    }

    public Material checkBlock(World world, int posX, int posY, int posZ) {
        return null;
    }
}
