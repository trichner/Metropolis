package ch.k42.metropolis.grid.urbanGrid.provider;

import org.bukkit.Material;
import org.bukkit.World;

/**
 * Natural decay checker
 *
 * @author Spaceribs
 */
public class NormalEnvironmentProvider extends EnvironmentProvider {

    public NormalEnvironmentProvider(Long newSeed) {
        super(newSeed);
    }

    public Material checkBlock(World world, int posX, int posY, int posZ) {
        return null;
    }
}
