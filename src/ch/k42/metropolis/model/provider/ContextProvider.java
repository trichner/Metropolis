package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.VoronoiGenerator;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.minions.GridRandom;
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
     *
     * I'm not sure how to implement it atm, using the vanilla biomes would be an option.
     * Or generate my own Voroni Noise ( https://forums.bukkit.org/threads/wgen-voronoi-noise.161319/ , http://shaneosullivan.wordpress.com/2007/04/05/fortunes-sweep-line-voronoi-algorithm-implemented-in-java/ )
     *
     * @param chunkX chunkSizeX coordinate
     * @param chunkZ chunkSizeZ coordinate
     * @return a Context to place there
     */
    public ContextType getContext(long seed, int chunkX, int chunkZ, GridRandom random){

        VoronoiGenerator gen1 = new VoronoiGenerator(seed, (short) 0);
        double frequency = 0.1; // the reciprocal of the distance between points
        double maxHeight = gen1.noise(chunkX, chunkZ, frequency);

        if (maxHeight < 0.2) {
            return ContextType.FARM;
        } else if (maxHeight < 0.4) {
            if (random.getChance(20)) {
                return ContextType.PARK;
            } else {
                return ContextType.RESIDENTIAL;
            }
        } else if (maxHeight < 0.6) {
            if (random.getChance(20)) {
                return ContextType.INDUSTRIAL;
            } else {
                return ContextType.LOWRISE;
            }
        } else if (maxHeight < 0.8) {
            return ContextType.MIDRISE;
        } else {
            return ContextType.HIGHRISE;
        }
    }
}
