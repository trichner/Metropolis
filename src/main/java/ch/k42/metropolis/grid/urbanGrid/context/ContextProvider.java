package ch.k42.metropolis.grid.urbanGrid.context;


import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.n1b.vector.Vec2D;

/**
 * Created by Thomas on 04.03.14.
 */
public interface ContextProvider {
    public ContextType getContext(ContextZone[] zones, int chunkX, int chunkZ, int level);
    public ContextType getContext(Vec2D place);
}
