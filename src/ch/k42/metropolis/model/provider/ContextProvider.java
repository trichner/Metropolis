package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
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

    public ContextType getContext(int chunkX,int chunkZ){
        return ContextType.HIGHRISE;
    }
}
