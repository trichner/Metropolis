package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import org.bukkit.Chunk;

/**
 * Represents a Parcel with no building.
 * @author Thomas Richner
 */
public class EmptyParcel extends Parcel {

    public EmptyParcel(Grid grid, int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ) {
        super(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ,ContextType.UNDEFINED);
        grid.fillParcels(chunkX,chunkZ,this);
    }

    @Override
    void populate(MetropolisGenerator generator, Chunk chunk) {
        // do nothing, since it's empty :)
    }
}
