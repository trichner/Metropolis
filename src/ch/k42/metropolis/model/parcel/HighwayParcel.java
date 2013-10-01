package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 10/1/13
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighwayParcel extends Parcel {

    public HighwayParcel(Grid grid, int chunkX, int chunkZ) {
        super(grid, chunkX, chunkZ, 1, 1, ContextType.HIGHWAY);
    }

    @Override
    public void populate(MetropolisGenerator generator, Chunk chunk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
