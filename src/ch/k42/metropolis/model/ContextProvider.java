package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;

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

    public ContextType getContext(int chunkX,int chunkZ){
        return ContextType.ALL;
    }
}
