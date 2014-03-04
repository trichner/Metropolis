package ch.k42.metropolis.grid.common;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.grid.urbanGrid.GridStatistics;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 16.09.13
 * Time: 00:44
 * To change this template use File | Settings | File Templates.
 */
abstract public class Grid {

    public static final int GRID_SIZE = GridProvider.GRID_SIZE;

    protected GridRandom random;
    protected GridProvider gridProvider;
    protected MetropolisGenerator generator;

    protected Cartesian2D root;

    protected Grid(GridRandom random, GridProvider gridProvider, MetropolisGenerator generator, Cartesian2D root) {
        this.random = random;
        this.gridProvider = gridProvider;
        this.generator = generator;
        this.root = root;
    }

    public GridRandom getRandom() {
        return random;
    }

    /**
     * Returns the parcel at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     *
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @return the Parcel at the given coordinate or null if there is none
     */
    public abstract Parcel getParcel(int chunkX, int chunkZ);

    /**
     * Places the parcel at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     *
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @param parcel a parcel to place
     * @return if the parcel was placed
     */
    public abstract void setParcel(int chunkX, int chunkZ, Parcel parcel);

    public abstract void fillParcels(int chunkX, int chunkZ, Parcel parcel);
}
