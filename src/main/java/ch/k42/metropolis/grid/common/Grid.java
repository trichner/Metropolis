package ch.k42.metropolis.grid.common;


import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
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

    protected final GridRandom random;
    protected final GridProvider gridProvider;

    protected final Cartesian2D root;

    protected Grid(GridRandom random, GridProvider gridProvider, Cartesian2D root) {
        this.random = random;
        this.gridProvider = gridProvider;
        this.root = root;
    }

    public abstract void populate(Chunk chunk);
    public void postPopulate(Chunk chunk){};

    public GridRandom getRandom() {
        return random;
    }

}
