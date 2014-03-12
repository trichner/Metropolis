package ch.k42.metropolis.grid.urbanGrid;

import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardProvider;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.Grid;
import ch.k42.metropolis.grid.common.GridProvider;
import ch.k42.metropolis.grid.urbanGrid.context.ContextProvider;
import ch.k42.metropolis.grid.urbanGrid.districts.District;
import ch.k42.metropolis.grid.urbanGrid.districts.IDistrict;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.urbanGrid.parcel.*;
import ch.k42.metropolis.grid.urbanGrid.statistics.AthmosStat;
import ch.k42.metropolis.grid.urbanGrid.statistics.GridStatistics;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.minions.Minions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.util.*;

/**
 * Represents a grid occupied fully with buildings
 *
 * @author Thomas Richner
 */
public class UrbanGrid extends Grid {

    private Parcel[][] parcels = new Parcel[GRID_SIZE][GRID_SIZE];

    private ContextProvider contextProvider;
    private GridStatistics statistics;
    private ClipboardProvider clipboardProvider;

    private Set<IDistrict> districtSet = new HashSet<>();

    public UrbanGrid(GridProvider provider, GridRandom random,MetropolisGenerator generator, Cartesian2D root) {
        super(random,provider,generator, root);
        this.statistics = new AthmosStat();
        contextProvider = generator.getContextProvider();
        this.clipboardProvider = generator.getClipboardProvider();
        placeHighways();
        recSetDistricts(new Cartesian2D(root.X + 1, root.Y + 1), new Cartesian2D(GRID_SIZE - 2, GRID_SIZE - 2));
        for(IDistrict district :districtSet){
            district.fillDistrict();
        }
        Bukkit.getLogger().info("Set new grid.");
    }

    private void placeHighways() { // places roads all around the grid

        int maxidx = GRID_SIZE - 1;

        // fill in the corners with Highway
        setParcel(0, 0, new HighwayParcel(this, root, RoadType.HIGHWAY_C_SE));
        setParcel(0, maxidx, new HighwayParcel(this, new Cartesian2D(root.X, root.Y + maxidx), RoadType.HIGHWAY_C_NE));
        setParcel(maxidx, 0, new HighwayParcel(this, new Cartesian2D(root.X + maxidx, root.Y), RoadType.HIGHWAY_C_SW));
        setParcel(maxidx, maxidx, new HighwayParcel(this, new Cartesian2D(root.X + maxidx, root.Y + maxidx), RoadType.HIGHWAY_C_NW));

        // fill in all highways
        for (int i = 1; i < maxidx; i++) {
            setParcel(0, i, new HighwayParcel(this, new Cartesian2D( root.X, root.Y + i), RoadType.HIGHWAY_SIDE_E)); //
            setParcel(i, 0, new HighwayParcel(this, new Cartesian2D(root.X + i, root.Y), RoadType.HIGHWAY_SIDE_S));
            setParcel(i, maxidx, new HighwayParcel(this, new Cartesian2D(root.X + i, root.Y + maxidx), RoadType.HIGHWAY_SIDE_N));
            setParcel(maxidx, i, new HighwayParcel(this, new Cartesian2D(root.X + maxidx, root.Y + i), RoadType.HIGHWAY_SIDE_W));
        }
    }

    @Override
    public void populate(Chunk chunk) {
        Parcel p = getParcel(chunk.getX(), chunk.getZ());
        if(p!=null){
            p.populate(generator, chunk);
        }else {
            Bukkit.getLogger().warning("Parcel not found");
        }
    }

    @Override
    public void postPopulate(Chunk chunk) {
        Parcel p = getParcel(chunk.getX(),chunk.getZ());
        if(p!=null){
            p.postPopulate(generator, chunk);
        }else {
            //Bukkit.getLogger().warning("Parcel found");
        }
    }



    public Parcel getParcel(int chunkX, int chunkZ) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        return parcels[x][z];
    }

    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        parcels[x][z] = parcel;
    }

    public void fillParcels(int chunkX, int chunkZ, Parcel parcel) {
        for (int x = 0; x < parcel.getChunkSizeX(); x++) {
            for (int z = 0; z < parcel.getChunkSizeZ(); z++) {
                setParcel(chunkX + x, chunkZ + z, parcel);
            }
        }
    }


    /**
     * Calculates the relative coordinates
     *
     * @param chunk
     * @return
     */
    private static int getChunkOffset(int chunk) {
        int ret = chunk % GRID_SIZE;
        if (ret < 0)
            ret += GRID_SIZE;
        return ret;
    }

    public GridStatistics getStatistics() {
        return statistics;
    }

    public ContextProvider getContextProvider() {
        return contextProvider;
    }

    private static final int blockSize = 14;


    public void recSetDistricts(Cartesian2D base,Cartesian2D size) {
        if (size.X > size.Y) {
            if (size.X < blockSize) {
                //recPartitionX(base,size);
                addDistrict(base, size);
            } else {
                int cut = Minions.makeCut(random, size.X);
                partitionXwithRoads(base,size,cut);
            }
        } else {
            if (size.Y < blockSize) { // No place for streets
                //place a new Block
                //recPartitionZ(base,size);
                addDistrict(base,size);
            } else {                 //put a street inbetween
                int cut = Minions.makeCut(random, size.Y);

                partitionZwithRoads(base,size,cut);
            }
        }
    } 
    private void addDistrict(Cartesian2D base,Cartesian2D size){
        IDistrict district = new District();
        district.initDistrict(base,size,this);
        districtSet.add(district);
    }

    private void partitionXwithRoads(Cartesian2D base,Cartesian2D initSize, int cut) {
        for (int i = base.Y; i < base.Y + initSize.Y; i++) {
            this.setParcel(base.X + cut, i, new RoadParcel(this,new Cartesian2D(base.X + cut, i)));
        }
        recSetDistricts(base,new Cartesian2D(cut,initSize.Y));
        recSetDistricts(new Cartesian2D(base.X+cut+1,base.Y),new Cartesian2D(initSize.X-cut-1,initSize.Y));
    }

    private void partitionZwithRoads(Cartesian2D base,Cartesian2D initSize, int cut) {
        for (int i = base.X; i < base.X + initSize.X; i++) {
            this.setParcel(i, base.Y + cut, new RoadParcel(this, new Cartesian2D(i, base.Y + cut)));
        }
        recSetDistricts(base,new Cartesian2D(initSize.X,cut));
        recSetDistricts(new Cartesian2D(base.X,base.Y+cut+1),new Cartesian2D(initSize.X,initSize.Y-cut-1));
    }

    @Override
    public String  toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("================ UrbanGrid ================\n");
        for(Parcel[] parr : parcels){
            for(Parcel p : parr){
                if(p==null){
                    sb.append("0");
                }else {
                    if(p instanceof StreetParcel){
                        sb.append("R");
                    }else{
                        sb.append("P");
                    }
                }
                sb.append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public ClipboardProvider getClipboardProvider() {
        return clipboardProvider;
    }
}
