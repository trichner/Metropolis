package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.VoronoiGenerator;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.minions.GridRandom;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.bukkit.World;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ContextProvider {

    private MetropolisGenerator generator;

    public ContextProvider(MetropolisGenerator generator) {
        this.generator = generator;
    }

    /**
     * TODO
     * Returns a context that should be placed on this coordinate
     * <p/>
     * I'm not sure how to implement it atm, using the vanilla biomes would be an option.
     * Or generate my own Voroni Noise ( https://forums.bukkit.org/threads/wgen-voronoi-noise.161319/ , http://shaneosullivan.wordpress.com/2007/04/05/fortunes-sweep-line-voronoi-algorithm-implemented-in-java/ )
     *
     * @param chunkX chunkSizeX coordinate
     * @param chunkZ chunkSizeZ coordinate
     * @return a Context to place there
     */
    public ContextType getContext(long seed, int chunkX, int chunkZ, GridRandom random) {

        SimplexOctaveGenerator gen1 = new SimplexOctaveGenerator(seed, 2);
        SimplexOctaveGenerator gen2 = new SimplexOctaveGenerator(seed, 2);

        double scale = 1;
        double scatterScale = 0.5;
        double holeScale = 0.01;
        double maxHeight = gen1.noise(chunkX * (holeScale * scale), chunkZ * (holeScale * scale), 0.3D, 0.6D, true);
        maxHeight += (gen1.noise(chunkX * (scatterScale * scale), chunkZ * (scatterScale * scale), 0.3D, 0.6D, true))/5;

        double altscale = 0.05;

        if (maxHeight < -0.3) {
            return ContextType.RESIDENTIAL;
        } else if (maxHeight < 0.3) {
            double alternate = gen2.noise(chunkX * (altscale * scale), chunkZ * (altscale * scale), 0.3D, 0.6D, true);
            if (alternate > 0.2) {
                return ContextType.INDUSTRIAL;
            } else {
                return ContextType.LOWRISE;
            }
        } else if (maxHeight < 0.6) {
            return ContextType.MIDRISE;
        } else {
            return ContextType.HIGHRISE;
        }
    }
}
