package ch.k42.metropolis.grid.common;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.UrbanGrid;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * This Class provides storage for all parcels.
 * It handles Grids and hides functionality if you are only intrested in a
 * parcel at a particular spot.
 *
 * @author Thomas Richner
 */
public class GridProvider {

    public static final int GRID_SIZE = 64; // DO NOT CHANGE! UNFORSEEN CONSEQUENCES...

    private Table<Integer,Integer,Grid> grids = HashBasedTable.create();

    private MetropolisGenerator generator;

    public GridProvider(MetropolisGenerator generator) {
        this.generator = generator;
    }

    /**
     * Returns the grid at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     *
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @return the grid at the given coordinate or null if there is none
     */
    public Grid getGrid(int chunkX, int chunkZ) {
        chunkX = getGridOrigin(chunkX);
        chunkZ = getGridOrigin(chunkZ);
        Grid grid = grids.get(chunkX,chunkZ);

        if (grid == null) { // does it exsist? or do we need to create one?
            grid = getNewGrid(chunkX, chunkZ); // This should be randomized and decoupled
            grids.put(chunkX,chunkZ, grid);
        }

        return grid;
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
}
