package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.VoronoiGenerator;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.zones.ContextZone;
import ch.k42.metropolis.plugin.ContextConfig;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.bukkit.World;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 20:06
 * To change this template use File | Settings | File Templates.
 */
public class ContextProvider {

    private ContextZone[] contextZones = {
        new ContextZone(ContextType.RESIDENTIAL, 1),
        new ContextZone(new ContextZone[] {
            new ContextZone(ContextType.LOWRISE, 3),
            new ContextZone(ContextType.INDUSTRIAL, 2)
        }, 1),
        new ContextZone(ContextType.MIDRISE, 1),
        new ContextZone(ContextType.HIGHRISE, 1)
    };

    private MetropolisGenerator generator;
    private SimplexOctaveGenerator gen1;
    private ContextConfig config;

    public ContextProvider(MetropolisGenerator generator, ContextConfig contextConfig) {
        this.generator = generator;
        this.config = contextConfig;
        this.gen1 = new SimplexOctaveGenerator(generator.getWorldSeed(), 2);
        this.contextZones = recurseWeights(contextZones);
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
    public ContextType getContext(int chunkX, int chunkZ, int level) {

        ContextType output = ContextType.UNDEFINED;

        double zone = gen1.noise(chunkX * config.getScale(level), chunkZ * config.getScale(level), 0.3D, 0.6D, true);
        zone += gen1.noise(chunkX * config.getScatterScale(), chunkZ * config.getScatterScale(), 0.3D, 0.6D, true)/config.getScatterAmount();

        double previousWeight = 0;

        for (ContextZone contextZone : contextZones) {

            double weightCheck = contextZone.getNormalizedWeight() + previousWeight;
                weightCheck = (weightCheck * 2) - 1;

            if (zone < weightCheck || zone > 1) {
                if (contextZone.hasChildren()) {
                    output = getContext(contextZone.getContextZones(), chunkX, chunkZ, level+1);
                } else {
                    output = contextZone.getContextType();
                }
                break;
            } else {
                previousWeight += contextZone.getNormalizedWeight();
            }
        }

        generator.getPlugin().getLogger().info(output.toString());

        return output;
    }

    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level) {

        ContextType output = ContextType.UNDEFINED;

        double zone = gen1.noise(chunkX * config.getScale(level), chunkZ * config.getScale(level), 0.3D, 0.6D, true);
        zone += gen1.noise(chunkX * config.getScatterScale(), chunkZ * config.getScatterScale(), 0.3D, 0.6D, true)/config.getScatterAmount();

        double previousWeight = 0;

        for (ContextZone contextZone : zones) {
            double weightCheck = (contextZone.getNormalizedWeight() * 2) - 1;
            weightCheck += previousWeight;

            if (zone < weightCheck || zone > 1) {
                if (contextZone.hasChildren()) {
                    output = getContext(contextZone.getContextZones(), chunkX, chunkZ, level+1);
                } else {
                    output = contextZone.getContextType();
                }
                break;
            } else {
                previousWeight = weightCheck;
            }
        }

        return output;
    }

    private ContextZone[] recurseWeights(ContextZone[] zones) {
        int totalWeights = 0;

        for (ContextZone contextZone : zones) {
            totalWeights += contextZone.getWeight();
        }

        for (ContextZone contextZone : zones) {
            contextZone.setTotalWeight(totalWeights);

            if (contextZone.hasChildren()) {
                contextZone.setContextZones(recurseWeights(contextZone.getContextZones()));
            }
        }

        return zones;
    }
}
