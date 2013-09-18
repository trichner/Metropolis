package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.ByteChunk;
import ch.k42.metropolis.minions.GridRandom;
import com.google.common.base.Optional;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 16.09.13
 * Time: 00:46
 * To change this template use File | Settings | File Templates.
 */
public class GridProvider {

    public static final int GRID_SIZE = 64;

    public void populate(MetropolisGenerator generator, ByteChunk byteChunk) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public static class GridKey{
        protected int gridX;
        protected int gridZ;
        public GridKey(int chunkX, int chunkZ) {
            this.gridX = chunkX/GRID_SIZE;
            this.gridZ = chunkZ/GRID_SIZE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GridKey gridKey = (GridKey) o;

            if (gridX != gridKey.gridX) return false;
            if (gridZ != gridKey.gridZ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = gridX;
            result = 31 * result + gridZ;
            return result;
        }
    }

    private Map<GridKey,Optional<Grid>> grids = new HashMap();
    private World world;

    public GridProvider(MetropolisGenerator generator) {
        this.world = generator.getWorld();
    }

    /**
     * Evaluates the Parcel at the given absolute chunk coordinates
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @return parcel at the given coordinate
     */
    public Parcel getParcel(int chunkX, int chunkZ){
        int x = chunkX%GRID_SIZE;
        int z = chunkZ%GRID_SIZE;
        x+=GRID_SIZE;
        z+=GRID_SIZE;
        x%=GRID_SIZE;
        z%=GRID_SIZE;
        return getGrid(chunkX,chunkZ).getParcel(x,z);
    }

    /**
     * Returns the grid at the given relative coordinates in the interval [0,Grid.GRIDSIZE)
     * @param chunkX chunk coordinates x
     * @param chunkZ chunk coordinates z
     * @return the grid at the given coordinate or null if there is none
     */
    public Grid getGrid(int chunkX, int chunkZ){
        GridKey key = new GridKey(chunkX,chunkZ);
        Optional<Grid> ogrid = grids.get(key);

        if(ogrid==null){ // does it exsist? or do we need to create one?
            ogrid = getNewGrid(chunkX,chunkZ); //TODO not always same grid
            grids.put(key,ogrid);
        }

        return ogrid.get();
    }
    /**
     * Places the Parcel at the given absolute chunk coordinates
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @param parcel at the given coordinate
     */
    public void setParcel(int chunkX, int chunkZ, Parcel parcel){
        int x = chunkX%GRID_SIZE;
        int z = chunkZ%GRID_SIZE;
        x+=GRID_SIZE;
        z+=GRID_SIZE;
        x%=GRID_SIZE;
        z%=GRID_SIZE;
        getGrid(chunkX,chunkZ).setParcel(x,z,parcel);
    }

    /**
     * Evaluates the Parcel at the given absolute chunk coordinates
     * @param chunkX x coordinate
     * @param chunkZ z coordinate
     * @return parcel at the given coordinate
     */
    public GridRandom getRandom(int chunkX, int chunkZ){
        return getGrid(chunkX,chunkZ).getRandom();
    }

    public void populate(MetropolisGenerator generator, Chunk chunk){
        getParcel(chunk.getX(),chunk.getZ()).populate(generator,chunk);
    }

    private Optional<Grid> getNewGrid(int chunkX,int chunkZ){
        long seed = world.getSeed();
        return Optional.of((Grid) new UrbanGrid(this,new GridRandom(seed,chunkX,chunkZ),chunkX,chunkZ)); // FIXME mooaaar grids
    }




}
