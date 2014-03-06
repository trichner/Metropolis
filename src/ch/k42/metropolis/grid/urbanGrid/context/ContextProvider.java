package ch.k42.metropolis.grid.urbanGrid.context;

import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.minions.Cartesian2D;

/**
 * Created by Thomas on 04.03.14.
 */
public interface ContextProvider {
    ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level);
    ContextType getContext(Cartesian2D place);
}
