package ch.k42.metropolis.grid.urbanGrid;

import ch.k42.metropolis.WorldEdit.Clipboard;
import ch.k42.metropolis.WorldEdit.ClipboardProvider;
import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.common.Grid;
import ch.k42.metropolis.grid.urbanGrid.districts.Context;
import ch.k42.metropolis.grid.urbanGrid.districts.District;
import ch.k42.metropolis.grid.urbanGrid.enums.ContextType;
import ch.k42.metropolis.grid.urbanGrid.enums.Direction;
import ch.k42.metropolis.grid.urbanGrid.parcel.*;
import ch.k42.metropolis.grid.urbanGrid.context.ContextProvider;
import ch.k42.metropolis.minions.Cartesian2D;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.grid.urbanGrid.enums.RoadType;
import ch.k42.metropolis.grid.common.GridProvider;
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

    private Set<Parcel> parcelSet = new HashSet<>();

    public UrbanGrid(GridProvider provider, GridRandom random,MetropolisGenerator generator, Cartesian2D root) {
        super(random,provider,generator, root);
        this.statistics = new AthmosStat();
        contextProvider = generator.getContextProvider();//new ContextProviderVoroni(random);
        this.clipboardProvider = generator.getClipboardProvider();
        placeHighways();
        recSetDistricts(new Cartesian2D(root.X + 1, root.Y + 1),new Cartesian2D(GRID_SIZE-2,GRID_SIZE-2));
        Bukkit.getLogger().info("Set new grid.");
        //Bukkit.getLogger().info(this.toString());
    }

    private void placeHighways() { // places roads all around the grid

        int maxidx = GRID_SIZE - 1;

        // fill in the corners with Highway
        setParcel(0, 0, new HighwayParcel(this, root.X, root.Y, RoadType.HIGHWAY_C_SE));
        setParcel(0, maxidx, new HighwayParcel(this, root.X, root.Y + maxidx, RoadType.HIGHWAY_C_NE));
        setParcel(maxidx, 0, new HighwayParcel(this, root.X + maxidx, root.Y, RoadType.HIGHWAY_C_SW));
        setParcel(maxidx, maxidx, new HighwayParcel(this, root.X + maxidx, root.Y + maxidx, RoadType.HIGHWAY_C_NW));

        // fill in all highways
        for (int i = 1; i < maxidx; i++) {
            setParcel(0, i, new HighwayParcel(this, root.X, root.Y + i, RoadType.HIGHWAY_SIDE_E)); //
            setParcel(i, 0, new HighwayParcel(this, root.X + i, root.Y, RoadType.HIGHWAY_SIDE_S));
            setParcel(i, maxidx, new HighwayParcel(this, root.X + i, root.Y + maxidx, RoadType.HIGHWAY_SIDE_N));
            setParcel(maxidx, i, new HighwayParcel(this, root.X + maxidx, root.Y + i, RoadType.HIGHWAY_SIDE_W));
        }
    }

    @Override
    public void populate(Chunk chunk) {
        Bukkit.getLogger().info("populating!");
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
    private static final int sigma_factor = 5;

    public void recSetDistricts(Cartesian2D base,Cartesian2D size) {

        if (size.X > size.Y) {
            if (size.X < blockSize) {
                //Context context = Context.getRandom(this,base);

                //districts.add(new District(base,size,context));
                recPartitionX(base,size);
            } else {
                int cut = makeCut(size.X);
                partitionXwithRoads(base,size,cut);
            }
        } else {
            if (size.Y < blockSize) { // No place for streets
                //place a new Block
                recPartitionZ(base,size);
            } else {                 //put a street inbetween
                int cut = makeCut(size.Y);
                partitionZwithRoads(base,size,cut);
            }
        }

    }

    private boolean placeRandom(Cartesian2D base,Cartesian2D size){
        Direction direction = findRoad(base,size,random);
        ContextType context = contextProvider.getContext(base);
        List<Clipboard> clips = clipboardProvider.getFit(size,context,direction);
        if(clips.size()!=0){ // can we place anything?
            Collections.shuffle(clips);
            for(Clipboard c : clips){
                if(random.getChance(c.getConfig().getOddsOfAppearance())){
                    parcelSet.add(new ClipboardParcel(this, base.X, base.Y, size.X, size.Y, c, context, direction));
                    return true;
                }
            }

        }
        return false;
    }

    private boolean placeRandomForSure(Cartesian2D base,Cartesian2D size){
        Direction direction = findRoad(base,size,random);
        ContextType context = contextProvider.getContext(base);
        List<Clipboard> clips = clipboardProvider.getFit(size,context,direction);
        if(clips.size()!=0){ // can we place anything?
            Collections.shuffle(clips);

            for(int i=0;i<20;i++){
                for(Clipboard c : clips){
                    if(random.getChance(c.getConfig().getOddsOfAppearance())){
                        parcelSet.add(new ClipboardParcel(this, base.X, base.Y, size.X, size.Y, c, context, direction));
                        return true;
                    }
                }
            }
            Bukkit.getLogger().info("Couldn't place schem for sure! Tried 20 times, but all odds failed me.");

        }
        return false;
    }

    private void recPartitionX(Cartesian2D base, Cartesian2D size){
        // place schem?
        if(placeRandom(base,size)){
           return;
        }

        if(size.X==1){ // can't make smaller
            if(!placeRandomForSure(base,size))
                parcelSet.add(new EmptyParcel(this,base.X,base.Y,size.X,size.Y));
        }else {
            int cut = makeCut(size.X);
            recPartitionZ(base, new Cartesian2D(cut, size.Y));
            recPartitionZ(new Cartesian2D(base.X + cut, base.Y), new Cartesian2D(size.X - cut, size.Y));
        }
    }

    private void recPartitionZ(Cartesian2D base, Cartesian2D size){
        if(!placeRandom(base,size)){
            return;
        }
        if(size.Y==1){ // can't make smaller
            if(!placeRandomForSure(base,size))
                parcelSet.add(new EmptyParcel(this,base.X,base.Y,size.X,size.Y));
        }else {
            int cut = makeCut(size.Y);
            recPartitionX(base, new Cartesian2D(size.X, cut));
            recPartitionX(new Cartesian2D(base.X , base.Y+ cut), new Cartesian2D(size.X, size.Y - cut));
        }

    }

    private Direction findRoad(Cartesian2D base,Cartesian2D size,GridRandom random) {
        List<Direction> directions = new LinkedList<>();

        if(Parcel.isStreet(this.getParcel(base.X, base.Y - 1)))
            directions.add(Direction.NORTH);

        if(Parcel.isStreet(this.getParcel(base.X, base.Y + size.Y)))
            directions.add(Direction.SOUTH);

        if(Parcel.isStreet(this.getParcel(base.X - 1, base.Y)))
            directions.add(Direction.WEST);

        if(Parcel.isStreet(this.getParcel(base.X + size.X, base.Y)))
            directions.add(Direction.EAST);

        if(directions.size()==0) return Direction.NONE;

        return directions.get(random.getRandomInt(directions.size()));
    }


    private final int makeCut(int x){
        double  mean = x / 2.0;
        double sigma = mean / sigma_factor;
        int      cut = getNormalCut(mean, sigma);
        return Minions.limit(x - 2, cut) + 1;
    }

    private final int getNormalCut(double mean, double sigma) {
        return (int) Math.round(mean + random.getRandomGaussian() * sigma);
    }

    private void partitionXwithRoads(Cartesian2D base,Cartesian2D initSize, int cut) {
        for (int i = base.Y; i < base.Y + initSize.Y; i++) {
            this.setParcel(base.X + cut, i, new RoadParcel(this, base.X + cut, i));
        }
        recSetDistricts(base,new Cartesian2D(cut,initSize.Y));
        recSetDistricts(new Cartesian2D(base.X+cut+1,base.Y),new Cartesian2D(initSize.X-cut-1,initSize.Y));
    }

    private void partitionZwithRoads(Cartesian2D base,Cartesian2D initSize, int cut) {
        for (int i = base.X; i < base.X + initSize.X; i++) {
            this.setParcel(i, base.Y + cut, new RoadParcel(this, i, base.Y + cut));
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
}
