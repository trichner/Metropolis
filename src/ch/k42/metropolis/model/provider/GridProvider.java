package ch.k42.metropolis.model.provider;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.grid.Grid;
import ch.k42.metropolis.model.grid.UrbanGrid;
import ch.k42.metropolis.model.parcel.Parcel;
import com.google.common.base.Optional;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * This Class provides storage for all parcels.
 * It handles Grids and hides functionality if you are only intrested in a
 * parcel at a particular spot.
 *
 * @author Thomas Richner
 */
public class GridProvider {

    public static final int GRID_SIZE = 64;

    public static class GridKey{
        protected int gridX;
        protected int gridZ;
        public GridKey(int chunkX, int chunkZ) {
            gridX = getGridOrigin(chunkX);
            gridZ = getGridOrigin(chunkZ);
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
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
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
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
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
        Parcel p = getParcel(chunk.getX(),chunk.getZ());
        if(p!=null){
            p.populate(generator,chunk);
        }else {
            generator.reportDebug("found empty Parcel: ["+chunk.getX()+"]["+chunk.getZ()+"]");
        }
    }

    private Optional<Grid> getNewGrid(int chunkX,int chunkZ){

        int originX = getGridOrigin(chunkX);
        int originZ = getGridOrigin(chunkZ);

        long seed = world.getSeed();
        return Optional.of((Grid) new UrbanGrid(this,new GridRandom(seed,originX,originZ),originX,originZ)); // FIXME mooaaar grids
    }

    private static int getGridOrigin(int chunk){
        int ret;
        if(chunk<0){
            chunk += 1;
            ret = (chunk) - ((chunk)%GRID_SIZE);
            ret -= GRID_SIZE;
        }else {
            ret =  chunk-(chunk%GRID_SIZE);
        }
        return ret;
    }

    private static int getChunkOffset(int chunk){
        int ret = chunk % GRID_SIZE;
        if(ret<0)
            ret+=GRID_SIZE;
        return ret;
    }

}
