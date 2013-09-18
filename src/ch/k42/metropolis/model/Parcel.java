package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.GridRandom;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class Parcel {

    protected int chunkX;
    protected int chunkZ;

    protected int chunkSizeX;
    protected int chunkSizeZ;

    protected ContextType contextType;
    protected Grid grid;


    protected Parcel(Grid grid,int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ,ContextType contextType) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.chunkSizeX = chunkSizeX;
        this.chunkSizeZ = chunkSizeZ;
        this.grid = grid;
        this.contextType = contextType;
    }

    abstract void populate(MetropolisGenerator generator,Chunk chunk);


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
                "chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                ", chunkSizeX=" + chunkSizeX +
                ", chunkSizeZ=" + chunkSizeZ +
                ", contextType=" + contextType +
                '}';
    }
}
