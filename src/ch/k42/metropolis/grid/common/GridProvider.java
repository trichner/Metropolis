package ch.k42.metropolis.grid.common;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * This Class provides storage for all parcels.
 * It handles Grids and hides functionality if you are only intrested in a
 * parcel at a particular spot.
 *
 * @author Thomas Richner
 */
public class GridProvider {

    public static final int GRID_SIZE = 64; // DO NOT CHANGE! UNFORSEEN CONSEQUENCES...

    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        Parcel p = getParcel(chunk.getX(), chunk.getZ());
        Parcel pN = getParcel(chunk.getX(), chunk.getZ() - 1);
        Parcel pS = getParcel(chunk.getX(), chunk.getZ() + 1);
        Parcel pE = getParcel(chunk.getX() + 1, chunk.getZ());
        Parcel pW = getParcel(chunk.getX() - 1, chunk.getZ());
        if (p != null) {
            p.postPopulate(generator, chunk);
            if (pN != null) pN.postPopulate(generator, chunk);
            if (pS != null) pS.postPopulate(generator, chunk);
            if (pE != null) pE.postPopulate(generator, chunk);
            if (pW != null) pW.postPopulate(generator, chunk);
        } else {
            generator.reportDebug("found empty Parcel: [" + chunk.getX() + "][" + chunk.getZ() + "]");
        }
    }

    private Table<Integer,Integer,Grid> grids = HashBasedTable.create();

    private MetropolisGenerator generator;

    public GridProvider(MetropolisGenerator generator) {
        this.generator = generator;
    }

    /**
     * Evaluates the Parcel at the given absolute chunk coordinates
     *
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @return parcel at the given coordinate
     */
    public Parcel getParcel(int chunkX, int chunkZ) {
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        return getGrid(chunkX, chunkZ).getParcel(x, z);
    }

    /**
     * Returns the grid at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     *
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @return the grid at the given coordinate or null if there is none
     */
    public Grid getGrid(int chunkX, int chunkZ) {
        Grid grid = grids.get(chunkX,chunkZ);

        if (grid == null) { // does it exsist? or do we need to create one?
            grid = getNewGrid(chunkX, chunkZ); // This should be randomized and decoupled
            grids.put(chunkX,chunkZ, grid);
        }

        return grid;
    }

    /**
     * Places the Parcel at the given absolute chunk coordinates
     *
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @param parcel at the given coordinate
     */
    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        getGrid(chunkX, chunkZ).setParcel(x, z, parcel);
    }

    /**
     * Evaluates the Parcel at the given absolute chunk coordinates
     *
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @return parcel at the given coordinate
     */
    public GridRandom getRandom(int chunkX, int chunkZ) {
        return getGrid(chunkX, chunkZ).getRandom();
    }

    public void populate(MetropolisGenerator generator, Chunk chunk) {
        Parcel p = getParcel(chunk.getX(), chunk.getZ());
        if (p != null) {
            p.populate(generator, chunk);
        } else {
            generator.reportDebug("found empty Parcel: [" + chunk.getX() + "][" + chunk.getZ() + "]");
        }
    }

    private Grid getNewGrid(int chunkX, int chunkZ) {

        int originX = getGridOrigin(chunkX);
        int originZ = getGridOrigin(chunkZ);

        long seed = generator.getWorldSeed();
        return new UrbanGrid(this, new GridRandom(seed, originX, originZ),generator, new Cartesian2D(originX,originZ)); // FIXME mooaaar grids
    }

    private static int getGridOrigin(int chunk) {
        int ret;
        if (chunk < 0) {
            chunk += 1;
            ret = (chunk) - ((chunk) % GRID_SIZE);
            ret -= GRID_SIZE;
        } else {
            ret = chunk - (chunk % GRID_SIZE);
        }
        return ret;
    }

    private static int getChunkOffset(int chunk) {
        int ret = chunk % GRID_SIZE;
        if (ret < 0)
            ret += GRID_SIZE;
        return ret;
    }

}
