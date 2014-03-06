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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a grid occupied fully with buildings
 *
 * @author Thomas Richner
 */
public class UrbanGrid extends Grid {
    private Parcel[][] parcels = new Parcel[GRID_SIZE][GRID_SIZE];

    private ContextProvider contextProvider;
    private GridStatistics statistics;
    private Set<District> districts = new TreeSet<>();

    public UrbanGrid(GridProvider provider, GridRandom random,MetropolisGenerator generator, Cartesian2D root) {
        super(random,provider,generator, root);
        this.statistics = new AthmosStat();
        contextProvider = generator.getContextProvider();//new ContextProviderVoroni(random);
        placeHighways();
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
    public Parcel getParcel(int chunkX, int chunkZ) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        return parcels[x][z];
    }

    @Override
    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);

        parcels[x][z] = parcel;

    }

    @Override
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
    private static final int sigma_factor = 6;

    public void recSetDistricts(Cartesian2D base,Cartesian2D size) {

        if (size.X > size.Y) {


            if (size.X < blockSize) { //FIXME Hardcoded
                Context context = Context.getRandom(this,base);
                districts.add(new District(base,size,context));
            } else {
                int cut = makeCut(size.X);
                partitionXwithRoads(base,size,cut);
            }
        } else {
            //FIXME Hardcoded

            if (size.Y < blockSize) { // No place for streets
                //place a new Block

            } else {                 //put a street inbetween
                int cut = makeCut(size.Y);
                partitionZwithRoads(base,size,cut);
            }
        }

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


//    private void dummy(){
//
//
//        /*
//         * TODO
//         *
//         * This function has grown too big & complex, it should be divided into understandable
//         * blocks
//         *
//         * 1. No iterations?
//         *
//         * 2. No streets?
//         *
//         * 3. Partion and go back to 1.
//         *
//         */
//        ClipboardProvider clips = generator.getClipboardProvider();
//
//        GridRandom random = this.getRandom();
//        ContextProvider context = this.getContextProvider();
//        Direction roadDir = findRoad(random);
//        boolean roadFacing = roadDir != Direction.NONE;
//
//        if (!roadFacing) {
//            roadDir = Direction.getRandomDirection(random);
//        }
//
//        // TODO Randomly choose size!
//
//        ContextType localContext = context.getContext(chunkX, chunkZ);
//        List<Clipboard> schems = clips.getFit(chunkSizeX, chunkSizeZ, localContext, roadDir, roadFacing); //just use context in one corner
//
//
//        int buildChance = generator.getPlugin().getMetropolisConfig().getBuildChance(); //schems.size() > 0 ? 80 - (65/(schems.size()+1)) : 0; // FIXME Not normalized!
//
//        //---- Randomly decide to place a schematic, first find one with correct orientation, if none found, place any that fits context
//        if (random.getChance(buildChance)) { //FIXME Hardcoded
//            if (schems != null && schems.size() > 0) {
//
//                //generator.reportDebug("Found " + schems.size() + " schematics for this spot, placing one");
//
//                Collections.shuffle(schems);
//
//                for(Clipboard c : schems){
//                    if(random.getChance(c.getSettings().getOddsOfAppearance())){
//                        parcel = new ClipboardParcel(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ, c, localContext, roadDir);
//                        parcel.populate(generator, chunk);
//                        return;
//                    }
//                }
//            }
//        }
//
//        //---
//        if ((chunkSizeX < 2) && (chunkSizeZ < 2)) { //no more iterations
//            schems = clips.getFit(chunkSizeX, chunkSizeZ, context.getContext(chunkX, chunkZ), roadDir, roadFacing); //just use context in one corner
//            if (schems != null && schems.size() > 0) {
//                //generator.reportDebug("Found " + schems.size() + " schematics for this spot, placing one");
//                Clipboard schem = schems.get(random.getRandomInt(schems.size()));
//                while (!random.getChance(schem.getSettings().getOddsOfAppearance())) {
//                    schem = schems.get(random.getRandomInt(schems.size()));
//                }
//                parcel = new ClipboardParcel(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ, schem, context.getContext(chunkX, chunkZ), roadDir);
//                parcel.populate(generator, chunk);
//                return;
//            } else { //=====FALLBACK
//                //generator.reportDebug("No schems found for size " + chunkSizeX + "x" + chunkSizeZ + " , context=" + context.getContext(chunkX, chunkZ) + "going over to fallback");
//                //FALLBACK
//                schems = clips.getFit(chunkSizeX, chunkSizeZ, context.getContext(chunkX, chunkZ), roadDir, false); //just use context in one corner
//                if (schems != null && schems.size() > 0) {
//                    //generator.reportDebug("Found " + schems.size() + " schematics for this spot, placing one");
//                    Clipboard schem = schems.get(random.getRandomInt(schems.size()));
//                    while (!random.getChance(schem.getSettings().getOddsOfAppearance())) {
//                        schem = schems.get(random.getRandomInt(schems.size()));
//                    }
//                    parcel = new ClipboardParcel(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ, schem, context.getContext(chunkX, chunkZ), roadDir);
//                    parcel.populate(generator, chunk);
//                    grid.getStatistics().logSchematic(schem);    // make log entry
//                    return;
//                }
//                parcel = new EmptyParcel(grid, chunkX, chunkZ, chunkSizeX, chunkSizeZ);
//                generator.reportDebug("No schems found for size " + chunkSizeX + "x" + chunkSizeZ + " , context=" + context.getContext(chunkX, chunkZ));
//
//            }
//            return; // in every case! we can't partition more! 1x1 should be available
//        }
//
//        final int blockSize = 14;
//        final int sigma_factor = 6;
//
//        //generator.reportDebug("chunkSizeX: " + chunkSizeX + ", chunkSizeZ: " + chunkSizeZ);
//
//        // Failed? partition into 2 sub lots
//        if (chunkSizeX > chunkSizeZ) { //if(sizeX>sizeZ){ // cut longer half, might prevent certain sizes to occur
//
//            double mean = chunkSizeX / 2.0;
//            double sigma = mean / sigma_factor;
//            int cut = getNormalCut(mean, sigma, random); //random.getRandomInt(1,chunkSizeX-1);
//
//            if (cut < 1)
//                cut = 1;
//            else if (cut > chunkSizeX - 2)
//                cut = chunkSizeX - 2;
//
//            //partitionX(grid,cut);
//            if (chunkSizeX < blockSize) { //FIXME Hardcoded
//
//                if (chunkSizeX > 8) {
//                    if (random.getChance(50)) {
//                        partitionX(grid, 6);
//                    } else {
//                        partitionX(grid, chunkSizeX - 6);
//                    }
//                } else if (chunkSizeX > 4) {
//                    int offset = random.getRandomInt(1, chunkSizeX-1);
//                    partitionX(grid, offset);
//                } else {
//                    partitionX(grid, 1);
//                }
//
//            } else {
//
//
//                partitionXwithRoads(grid, cut);
//            }
//        } else {
//            //FIXME Hardcoded
//            double  mean = chunkSizeZ / 2.0;
//            double sigma = mean / sigma_factor;
//            int      cut = getNormalCut(mean, sigma, random);
//
//            if (cut < 1) // sanitize cut
//                cut = 1;
//            else if (cut > chunkSizeZ - 2)
//                cut = chunkSizeZ - 2;
//
//            //partitionZ(grid,cut);
//            if (chunkSizeZ < blockSize) { // No place for streets
//
//                if (chunkSizeZ > 8) {
//                    if (random.getChance(50)) {
//                        partitionZ(grid, 6);
//                    } else {
//                        partitionZ(grid, chunkSizeZ - 6);
//                    }
//                } else if (chunkSizeZ > 4) {
//                    int offset = random.getRandomInt(1, chunkSizeZ-1);
//                    partitionZ(grid, offset);
//                } else {
//                    partitionZ(grid, 1);
//                }
//
//            } else {                 //put a street inbetween
//                partitionZwithRoads(grid, cut);
//            }
//        }
//        partition1.populate(generator, chunk);
//        partition2.populate(generator, chunk);
//    }
//
//    private Direction findRoad(GridRandom random) {
//
//        boolean northP = this.getParcel(chunkX, chunkZ - 1).getContextType().equals(ContextType.STREET) ||
//                this.getParcel(chunkX, chunkZ - 1).getContextType().equals(ContextType.HIGHWAY);
//
//        boolean southP = this.getParcel(chunkX, chunkZ + chunkSizeZ).getContextType().equals(ContextType.STREET) ||
//                this.getParcel(chunkX, chunkZ + chunkSizeZ).getContextType().equals(ContextType.HIGHWAY);
//
//        boolean westP = this.getParcel(chunkX - 1, chunkZ).getContextType().equals(ContextType.STREET) ||
//                this.getParcel(chunkX - 1, chunkZ).getContextType().equals(ContextType.HIGHWAY);
//
//        boolean eastP = this.getParcel(chunkX + chunkSizeX, chunkZ).getContextType().equals(ContextType.STREET) ||
//                this.getParcel(chunkX + chunkSizeX, chunkZ).getContextType().equals(ContextType.HIGHWAY);
//
//        return Direction.getRandomDirection(random, northP, southP, eastP, westP); // haven't found any streets
//    }

}
