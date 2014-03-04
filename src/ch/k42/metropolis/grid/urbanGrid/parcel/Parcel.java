package ch.k42.metropolis.grid.urbanGrid.parcel;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.common.Grid;
import org.bukkit.Chunk;

/**
 * A Parcel represents one structure of Metropolis.
 *
 * @author Thomas Richner
 */
public abstract class Parcel {

    protected int chunkX;
    protected int chunkZ;

    protected int chunkSizeX;
    protected int chunkSizeZ;

    protected ContextType contextType;
    protected Grid grid;


    public Parcel(Grid grid, int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ, ContextType contextType) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkSizeX = chunkSizeX;
        this.chunkSizeZ = chunkSizeZ;
        this.grid = grid;
        this.contextType = contextType;
    }

    public abstract void populate(MetropolisGenerator generator, Chunk chunk);

    public abstract void postPopulate(MetropolisGenerator generator, Chunk chunk);


    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public int getChunkSizeX() {
        return chunkSizeX;
    }

    public void setChunkSizeX(int chunkSizeX) {
        this.chunkSizeX = chunkSizeX;
    }

    public int getChunkSizeZ() {
        return chunkSizeZ;
    }

    public void setChunkSizeZ(int chunkSizeZ) {
        this.chunkSizeZ = chunkSizeZ;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "chunkSizeX=" + chunkX +
                ", chunkSizeZ=" + chunkZ +
                ", chunkSizeX=" + chunkSizeX +
                ", chunkSizeZ=" + chunkSizeZ +
                ", contextType=" + contextType +
                '}';
    }
}
