package ch.k42.metropolis.grid.urbanGrid.context;

import org.bukkit.util.noise.SimplexOctaveGenerator;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.GridRandom;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class PopulationProvider {

    private MetropolisGenerator generator;

    public PopulationProvider(MetropolisGenerator generator) {
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

        double holeScale = 0.03;
        double maxHeight = gen1.noise(chunkX * holeScale, chunkZ * holeScale, 0.3D, 0.6D, true);

        double altscale = 0.1;
        double alternate = gen2.noise(chunkX * altscale, chunkZ * altscale, 0.3D, 0.6D, true);

//        VoronoiGenerator gen1 = new VoronoiGenerator(seed, (short) 2);
//        double frequency = 0.1; // the reciprocal of the distance between points
//        int size = 2;
//        double maxHeight = gen1.noise((chunkX+1600)/size, (chunkZ+1600)/size, frequency);

        if (maxHeight < -0.3) {
            return ContextType.RESIDENTIAL;
        } else if (maxHeight < 0.2) {
            if (alternate > 0.4) {
                return ContextType.INDUSTRIAL;
            } else {
                return ContextType.LOWRISE;
            }
        } else if (maxHeight < 0.5) {
            return ContextType.MIDRISE;
        } else {
            return ContextType.HIGHRISE;
        }
    }
}
