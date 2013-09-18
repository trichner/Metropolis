package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.Chunk;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 18.09.13
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class EmptyParcel extends Parcel {

    public EmptyParcel(Grid grid, int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ) {
        super(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ,ContextType.UNDEFINED);
        grid.fillParcels(chunkX+1,chunkZ+1,this);
    }

    @Override
    void populate(MetropolisGenerator generator, Chunk chunk) {
        // do nothing, since it's empty :)
    }
}
