package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.populators.MetropolisGenerator;
import ch.k42.metropolis.model.enums.ContextType;

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
    public ContextType getContext(int chunkX,int chunkZ){
        return ContextType.HIGHRISE;
    }
}
