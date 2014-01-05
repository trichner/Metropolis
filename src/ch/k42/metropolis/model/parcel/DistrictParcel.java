package ch.k42.metropolis.model.parcel;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.model.enums.Direction;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.WorldEdit.*;
import ch.k42.metropolis.model.provider.ContextProvider;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.grid.Grid;
import org.bukkit.Chunk;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 17.09.13
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class DistrictParcel extends Parcel {

    private DistrictParcel partition1; // if it gets partitioned, used this two to save them
    private DistrictParcel partition2;

    private Parcel parcel = null;    //it it doesn't get partitioned, only placed, use this

    public DistrictParcel(Grid grid,int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ, ContextType.UNDEFINED);
        grid.fillParcels(chunkX,chunkZ,this);
    }

    private Grid grid;
    private boolean fallback;

    public void populate(MetropolisGenerator generator,Chunk chunk) {
        fallback = generator.getPlugin().getMetropolisConfig().allowDirectionFallbackPlacing();
        ClipboardProvider clips = generator.getClipboardProvider();
        grid = generator.getGridProvider().getGrid(chunkX,chunkZ);
        GridRandom random = grid.getRandom();
        ContextProvider context = generator.getContextProvider();
        Direction roadDir = findRoad();
        boolean roadFacing = roadDir != Direction.NONE;

        if (!roadFacing) {
            roadDir = Direction.getRandomDirection();
        }

        // TODO Randomly choose size!

        //---- Randomly decide to place a schem, fist find one with correct orientation, if none found, place any that fits context
        if(random.getChance(60)){ //FIXME Hardcoded
            ContextType localContext = context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random);
            List<Clipboard> schems = clips.getFit(chunkSizeX, chunkSizeZ, localContext, roadDir, roadFacing); //just use context in one corner
            if(schems!=null&&schems.size()>0){
                generator.reportDebug("Found "+schems.size()+" schematics for this spot, placing one");
                parcel = new ClipboardParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,schems.get(random.getRandomInt(schems.size())), localContext, roadDir);
                parcel.populate(generator,chunk);
                return;
            }else { // find a schematic, but ignore road
                generator.reportDebug("No schems found for size "+chunkSizeX+"x"+chunkSizeZ + " , context=" + localContext + "going over to fallback");
                //FALLBACK
                if(fallback){
                    schems = clips.getFit(chunkSizeX,chunkSizeZ, context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random), roadDir, roadFacing); //just use context in one corner //TODO use Direction.NONE
                    if(schems!=null&&schems.size()>0){
                        generator.reportDebug("Found "+schems.size()+" schematics for this spot, placing one");
                        parcel = new ClipboardParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,schems.get(random.getRandomInt(schems.size())), localContext, roadDir);
                        parcel.populate(generator,chunk);
                        return;
                    }else {
                        generator.reportDebug("No schems found for size "+chunkSizeX+"x"+chunkSizeZ + " , context=" + localContext);
                    }
                }
            }
        }

        //---
        if((chunkSizeX<2)&&(chunkSizeZ<2)){ //no more iterations
            List<Clipboard> schems = clips.getFit(chunkSizeX,chunkSizeZ,context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random), roadDir, roadFacing); //just use context in one corner
            if(schems!=null&&schems.size()>0){
                generator.reportDebug("Found "+schems.size()+" schematics for this spot, placing one");
                parcel = new ClipboardParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,schems.get(random.getRandomInt(schems.size())),context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random), roadDir);
                parcel.populate(generator,chunk);
                return;
            }else {
                generator.reportDebug("No schems found for size "+chunkSizeX+"x"+chunkSizeZ + " , context=" + context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random) + "going over to fallback");
                //FALLBACK
                schems = clips.getFit(chunkSizeX,chunkSizeZ, context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random), roadDir, roadFacing); //just use context in one corner
                if(schems!=null&&schems.size()>0){
                    generator.reportDebug("Found "+schems.size()+" schematics for this spot, placing one");
                    parcel = new ClipboardParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,schems.get(random.getRandomInt(schems.size())),context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random), roadDir);
                    parcel.populate(generator,chunk);
                    return;
                }
                parcel = new EmptyParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ);
                generator.reportDebug("No schems found for size "+chunkSizeX+"x"+chunkSizeZ + " , context=" + context.getContext(generator.getWorldSeed(), chunkX, chunkZ, random));

            }
            return; // in every case! we can't partition more! 1x1 should be available
        }

        final int blockSize=14;

        // Failed? partition into 2 sub lots
        if(chunkSizeX>chunkSizeZ){//if(sizeX>sizeZ){ // cut longer half, might prevent certain sizes to occur
            double mean  = chunkSizeX/2.0;
            double sigma = mean/4.0;
            int cut = getNormalCut(mean,sigma,random); //random.getRandomInt(1,chunkSizeX-1);

            //partitionX(grid,cut);
            if(chunkSizeX<blockSize){ //FIXME Hardcoded
                if(cut<1)
                    cut=1;
                else if(cut>chunkSizeX-1)
                    cut=chunkSizeX-1;

                partitionX(grid,cut);
            }else {
                if(cut<1)
                    cut=1;
                else if(cut>chunkSizeX-2)
                    cut=chunkSizeX-2;

                partitionXwithRoads(grid,cut);
            }
        }else {
            double mean  = chunkSizeZ/2.0;
            double sigma = mean/2.0;
            int cut = getNormalCut(mean,sigma,random);

            //partitionZ(grid,cut);
            if(chunkSizeZ<blockSize){ // No place for streets
                if(cut<1)
                    cut=1;
                else if(cut>chunkSizeZ-1)
                    cut=chunkSizeZ-1;

                partitionZ(grid,cut);
            }else {                 //put a street inbetween
                if(cut<1)
                    cut=1;
                else if(cut>chunkSizeZ-2)
                    cut=chunkSizeZ-2;

                partitionZwithRoads(grid,cut);
            }
        }
        partition1.populate(generator,chunk);
        partition2.populate(generator,chunk);
    }

    @Override
    public void postPopulate(MetropolisGenerator generator, Chunk chunk) {
        partition1.postPopulate(generator,chunk);
        partition2.postPopulate(generator,chunk);
    }

    private int getNormalCut(double mean, double sigma, GridRandom random){
        return  (int) Math.round(mean+random.getRandomGaussian()*sigma);
    }

    private Direction findRoad(){

        boolean northP = grid.getParcel(chunkX,chunkZ-1).getContextType().equals(ContextType.STREET) ||
                grid.getParcel(chunkX,chunkZ-1).getContextType().equals(ContextType.HIGHWAY);

        boolean southP = grid.getParcel(chunkX,chunkZ+chunkSizeZ).getContextType().equals(ContextType.STREET) ||
                grid.getParcel(chunkX,chunkZ+chunkSizeZ).getContextType().equals(ContextType.HIGHWAY);

        boolean westP = grid.getParcel(chunkX-1,chunkZ).getContextType().equals(ContextType.STREET) ||
                grid.getParcel(chunkX-1,chunkZ).getContextType().equals(ContextType.HIGHWAY);

        boolean eastP = grid.getParcel(chunkX+chunkSizeX,chunkZ).getContextType().equals(ContextType.STREET) ||
                grid.getParcel(chunkX+chunkSizeX,chunkZ).getContextType().equals(ContextType.HIGHWAY);

        if (northP) {
            return Direction.NORTH;
        } else if (southP) {
            return Direction.SOUTH;
        } else if (eastP) {
            return Direction.EAST;
        } else if (westP) {
            return Direction.WEST;
        }

        return Direction.NONE; // haven't found any streets
    }

    private void partitionXwithRoads(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX,chunkZ,cut,chunkSizeZ);
        for(int i=chunkZ;i<chunkZ+chunkSizeZ;i++){
            grid.setParcel(chunkX+cut,i,new RoadParcel(grid,chunkX+cut,i));
        }
        partition2 = new DistrictParcel(grid,chunkX+cut+1,chunkZ,chunkSizeX-cut-1,chunkSizeZ);
    }
    private void partitionX(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX,chunkZ,cut,chunkSizeZ);
        partition2 = new DistrictParcel(grid,chunkX+cut,chunkZ,chunkSizeX-cut,chunkSizeZ);
    }

    private void partitionZwithRoads(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX,chunkZ,chunkSizeX,cut);
        for(int i=chunkX;i<chunkX+chunkSizeX;i++){
            grid.setParcel(i,chunkZ+cut,new RoadParcel(grid,i,chunkZ+cut));
        }
        partition2 = new DistrictParcel(grid,chunkX,chunkZ+cut+1,chunkSizeX,chunkSizeZ-cut-1);
    }
    private void partitionZ(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX,chunkZ,chunkSizeX,cut);
        partition2 = new DistrictParcel(grid,chunkX,chunkZ+cut,chunkSizeX,chunkSizeZ-cut);
    }
}
