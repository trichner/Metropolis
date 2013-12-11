package ch.k42.metropolis.model.grid;

import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.parcel.Parcel;
import ch.k42.metropolis.model.provider.GridProvider;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 16.09.13
 * Time: 00:44
 * To change this template use File | Settings | File Templates.
 */
abstract public class Grid {

    public static final int GRID_SIZE = GridProvider.GRID_SIZE;

    protected GridStatistics statistics;
    protected GridRandom random;
    protected GridProvider gridProvider;

    protected int chunkX;
    protected int chunkZ;

    public Grid(GridProvider gridProvider,GridRandom random,int chunkX,int chunkZ) {
        this.random = random;
        this.gridProvider = gridProvider;
        this.statistics = new GridStatistics();
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public GridStatistics getStatistics() {
        return statistics;
    }

    public GridRandom getRandom() {
        return random;
    }

    /**
     * Returns the parcel at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @return the Parcel at the given coordinate or null if there is none
     */
    public abstract Parcel getParcel(int chunkX, int chunkZ);
    /**
     * Places the parcel at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @param parcel a parcel to place
     * @return if the parcel was placed
     */
    public abstract void setParcel(int chunkX, int chunkZ, Parcel parcel);

    public abstract void fillParcels(int chunkX,int chunkZ, Parcel parcel);
}
