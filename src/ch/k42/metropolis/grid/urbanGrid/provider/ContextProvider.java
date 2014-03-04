package ch.k42.metropolis.grid.urbanGrid.provider;

import ch.k42.metropolis.grid.urbanGrid.ContextZone;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;

/**
 * Created by Thomas on 04.03.14.
 */
public interface ContextProvider {
    ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level);
    ContextType getContext(int chunkX, int chunkZ);
}
