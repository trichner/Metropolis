package ch.k42.metropolis.grid.urbanGrid.provider;

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

    public abstract Material checkBlock(World world, int posX, int posY, int posZ);
}
