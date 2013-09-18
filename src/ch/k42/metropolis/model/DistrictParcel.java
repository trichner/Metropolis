package ch.k42.metropolis.model;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.minions.Direction;
import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.WorldEdit.*;
import org.bukkit.Chunk;
import org.bukkit.World;


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

    private ClipboardParcel parcel = null;    //it it doesn't get partitioned, only placed, use this

    public DistrictParcel(Grid grid,int chunkX, int chunkZ, int chunkSizeX, int chunkSizeZ) {
        super(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ);
    }

    private Grid grid;

    public void populate(MetropolisGenerator generator,Chunk chunk) {

        ClipboardProviderWorldEdit clips = generator.getClipboardProvider();
        grid = generator.getGridProvider().getGrid(chunkX,chunkZ);
        GridRandom random = grid.getRandom();
        ContextProvider context = generator.getContextProvider();

        // TODO Randomly choose size!

        if(random.getChance(60)){
            List<Clipboard> schems = clips.getFit(chunkSizeX,chunkSizeZ, findRoad(),context.getContext(chunkX,chunkZ)); //just use context in one corner
            if(schems!=null&&schems.size()>0){
                parcel = new ClipboardParcel(grid,chunkX,chunkZ,chunkSizeX,chunkSizeZ,schems.get(random.getRandomInt(schems.size())));
                parcel.populate(generator,chunk);
                return;
            }
        }


        if((chunkSizeX<=1)&&(chunkSizeZ<=1)){ //no more iterations
            return;
        }


        // Failed? partition into 2 sub lots
        if(chunkSizeX>chunkSizeZ){//if(sizeX>sizeZ){ // cut longer half, might prevent certain sizes to occure
            int cut = random.getRandomInt(1,chunkSizeX-1);
            if(chunkSizeX<5){
                partitionX(grid,cut);
            }else {
                partitionXwithRoads(grid,cut);
            }
        }else {
            int cut = random.getRandomInt(1,chunkSizeX-1);
            if(chunkSizeZ<5){
                partitionZ(grid,cut);
            }else {
                partitionZwithRoads(grid,cut);
            }
        }
        partition1.populate(generator,chunk);
        partition2.populate(generator,chunk);
    }


    //==== -1 should be fine, since there 'should' be roads all around
    private Direction findRoad(){
        for(int i=0;i<chunkSizeX;i++){
            Parcel p = grid.getParcel(chunkX&Grid.GRID_SIZE+i,chunkZ&Grid.GRID_SIZE-1);
            if(p!=null && p.getContextType().equals(ContextType.ROAD)){
                return Direction.NORTH;
            }
            p = grid.getParcel(chunkX&Grid.GRID_SIZE+i,chunkZ&Grid.GRID_SIZE+chunkSizeZ);
            if(p!=null && p.getContextType().equals(ContextType.ROAD)){
                return Direction.SOUTH;
            }
        }
        for(int i=0;i<chunkSizeZ;i++){
            Parcel p = grid.getParcel(chunkX&Grid.GRID_SIZE-1,chunkZ&Grid.GRID_SIZE+i);
            if(p!=null && p.getContextType().equals(ContextType.ROAD)){
                return Direction.WEST;
            }
            p = grid.getParcel(chunkX&Grid.GRID_SIZE+chunkSizeX+1,chunkZ&Grid.GRID_SIZE);
            if(p!=null && p.getContextType().equals(ContextType.ROAD)){
                return Direction.EAST;
            }
        }
        return Direction.NONE; // haven't found any streets
    }

    private void partitionXwithRoads(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE,cut,chunkSizeZ);
        for(int i=chunkZ;i<chunkZ+chunkSizeZ;i++){
            grid.setParcel(chunkX%Grid.GRID_SIZE,i%Grid.GRID_SIZE,new RoadParcel(grid,chunkX,i));
        }
        partition2 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE+cut+1,chunkZ%Grid.GRID_SIZE,chunkSizeX-cut-1,chunkSizeZ);
    }
    private void partitionX(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE,cut,chunkSizeZ);
        partition2 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE+cut,chunkZ%Grid.GRID_SIZE,chunkSizeX-cut,chunkSizeZ);
    }


    private void partitionZwithRoads(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE,chunkSizeX,cut);
        for(int i=chunkX;i<chunkX+chunkSizeX;i++){
            grid.setParcel(i%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE+cut,new RoadParcel(grid,i,chunkZ));
        }
        partition2 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE+cut+1,chunkSizeX,chunkSizeZ-cut-1);
    }
    private void partitionZ(Grid grid,int cut){
        partition1 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE,chunkSizeX,cut);
        partition2 = new DistrictParcel(grid,chunkX%Grid.GRID_SIZE,chunkZ%Grid.GRID_SIZE+cut,chunkSizeX,chunkSizeZ-cut);
    }
}
