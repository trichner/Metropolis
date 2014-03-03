package ch.k42.metropolis.model.provider;

import org.bukkit.Material;
import org.bukkit.World;

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
