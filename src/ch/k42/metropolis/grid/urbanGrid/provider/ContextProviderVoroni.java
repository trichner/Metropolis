package ch.k42.metropolis.grid.urbanGrid.provider;

import ch.k42.metropolis.grid.urbanGrid.ContextZone;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;

/**
 * Created by Thomas on 04.03.14.
 */
public class ContextProviderVoroni implements ContextProvider {
    @Override
    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level) {
        return null;
    }

    @Override
    public ContextType getContext(int chunkX, int chunkZ) {
        return null;
    }
}
