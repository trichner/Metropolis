package ch.k42.metropolis.grid.urbanGrid.provider;

import org.bukkit.Material;
import org.bukkit.World;

import ch.k42.metropolis.minions.VoronoiGenerator;

/**
 * Natural decay checker
 *
 * @author Spaceribs
 */
public class NetherEnvironmentProvider extends EnvironmentProvider {

    VoronoiGenerator lava;

    public NetherEnvironmentProvider(Long seed) {
        super(seed);
        this.lava = new VoronoiGenerator(seed, (short) 0);
    }

    public Material checkBlock(World world, int posX, int posY, int posZ) {

        double frequency = 0.01; // the reciprocal of the distance between points
        double frequency2 = 0.1; // the reciprocal of the distance between points
        int size = 1;

        double maxHeight = lava.noise(posX / size, posZ / size, frequency);
        double innerHeight = lava.noise(posX / size, posZ / size, frequency2);

        double overlay = maxHeight + (innerHeight / 10);
        if (overlay < 0.06) {
            if (posY > 64) {
                return Material.AIR;
            } else {
                int cut = (int) (((0.06 - overlay) / 0.06) * 46);
                if (posY > 64 - cut) {
                    if (posY > 56) {
                        return Material.AIR;
                    } else {
                        return Material.LAVA;
                    }
                }
            }
        }

        return null;
    }
}
