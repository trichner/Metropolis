package ch.k42.metropolis.grid.urbanGrid.context;

import org.bukkit.util.noise.SimplexOctaveGenerator;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ContextProviderSimplex implements ContextProvider {

    private MetropolisGenerator generator;
    private ContextZone[] contextZones;
    private SimplexOctaveGenerator gen1;
    private ContextConfig config;

    public ContextProviderSimplex(MetropolisGenerator generator, ContextConfig contextConfig) {
        this.generator = generator;
        this.config = contextConfig;
        this.gen1 = new SimplexOctaveGenerator(generator.getWorldSeed(), 2);
        this.contextZones = recurseWeights(contextConfig.getContextZones());
    }

    @Override
    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level) {

        ContextType output = ContextType.UNDEFINED;

        double zone = gen1.noise(chunkX * config.getScale(level), chunkZ * config.getScale(level), 0.3D, 0.6D, true);
        zone += gen1.noise(chunkX * config.getScatterScale(), chunkZ * config.getScatterScale(), 0.3D, 0.6D, true)/config.getScatterAmount();

        double previousWeight = 0;

        for (ContextZone contextZone : zones) {

            double normal = (contextZone.getNormalizedWeight() * 2);
            double weightCheck = normal - 1;
            weightCheck += previousWeight;

            if (zone < weightCheck || zone > 1) {
                if (contextZone.hasChildren()) {
                    output = getContext(contextZone.getContextZones(), chunkX, chunkZ, level+1);
                } else {
                    output = contextZone.getContextType();
                }
                break;
            } else {
                previousWeight += normal;
            }

        }

        return output;
    }

    /**
     * Returns a context that should be placed on this coordinate
     * <p/>
     * I'm not sure how to implement it atm, using the vanilla biomes would be an option.
     * Or generate my own Voroni Noise ( https://forums.bukkit.org/threads/wgen-voronoi-noise.161319/ , http://shaneosullivan.wordpress.com/2007/04/05/fortunes-sweep-line-voronoi-algorithm-implemented-in-java/ )
     *
     * @param chunkX chunkSizeX coordinate
     * @param chunkZ chunkSizeZ coordinate
     * @return a Context to place there
     */

    @Override
    public ContextType getContext(Cartesian2D place) {
        return getContext(contextZones, place.X, place.Y, 1);
    }

    private ContextZone[] recurseWeights(ContextZone[] zones) {
        int totalWeights = 0;
        ContextZone[] output = zones;

        for (ContextZone contextZone : output) {
            totalWeights += contextZone.getWeight();
        }

        for (ContextZone contextZone : output) {
            contextZone.setTotalWeight(totalWeights);

            if (contextZone.hasChildren()) {
                contextZone.setContextZones(recurseWeights(contextZone.getContextZones()));
            }
        }

        return output;
    }
}
