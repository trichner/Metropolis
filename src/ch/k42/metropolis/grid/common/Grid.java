package ch.k42.metropolis.grid.common;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.grid.urbanGrid.GridStatistics;
import ch.k42.metropolis.grid.urbanGrid.parcel.Parcel;
import org.bukkit.Chunk;

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

    public abstract void populate(Chunk chunk);
    public void postPopulate(Chunk chunk){};

    public GridRandom getRandom() {
        return random;
    }

}
