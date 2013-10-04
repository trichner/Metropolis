package ch.k42.metropolis.model.grid;

import ch.k42.metropolis.minions.GridRandom;
import ch.k42.metropolis.model.enums.RoadType;
import ch.k42.metropolis.model.parcel.DistrictParcel;
import ch.k42.metropolis.model.parcel.HighwayParcel;
import ch.k42.metropolis.model.parcel.Parcel;
import ch.k42.metropolis.model.parcel.RoadParcel;
import ch.k42.metropolis.model.provider.GridProvider;

/**
 * Represents a grid occupied fully with buildings
 * @author Thomas Richner
 */
public class UrbanGrid extends Grid{
    private Parcel[][] parcels = new Parcel[GRID_SIZE][GRID_SIZE];

    private DistrictParcel district;

    public UrbanGrid(GridProvider provider,GridRandom random,int chunkX,int chunkZ) {
        super(provider,random,chunkX,chunkZ);
        placeHighways();
        district=new DistrictParcel(this,chunkX+1,chunkZ+1,GRID_SIZE-2,GRID_SIZE-2);
        //placeParcels(chunkX+1,chunkZ+1,GRID_SIZE-2,GRID_SIZE-2,);
        //fillParcels(chunkX+1,chunkZ+1,new EmptyParcel(this,chunkX+1,chunkZ+1,GRID_SIZE-2,GRID_SIZE-2));
    }

    private void placeHighways(){ // places roads all around the grid

        int maxidx = GRID_SIZE-1;

        setParcel(0,0,new HighwayParcel(this,chunkX,chunkZ, RoadType.HIGHWAY_CORNER_SE));
        setParcel(0,maxidx,new HighwayParcel(this,chunkX,chunkZ+maxidx, RoadType.HIGHWAY_CORNER_NE));
        setParcel(maxidx,0,new HighwayParcel(this,chunkX+maxidx,chunkZ, RoadType.HIGHWAY_CORNER_SW));
        setParcel(maxidx,maxidx,new HighwayParcel(this,chunkX+maxidx,chunkZ+maxidx, RoadType.HIGHWAY_CORNER_NW));

        for(int i=1;i<maxidx;i++){
            setParcel(0,i,new HighwayParcel(this,chunkX,chunkZ+i,RoadType.HIGHWAY_SIDE_E)); //
            setParcel(i,0,new HighwayParcel(this,chunkX+i,chunkZ,RoadType.HIGHWAY_SIDE_S));
            setParcel(i,maxidx,new HighwayParcel(this,chunkX+i,chunkZ+maxidx,RoadType.HIGHWAY_SIDE_N));
            setParcel(maxidx,i,new HighwayParcel(this,chunkX+maxidx,chunkZ+i,RoadType.HIGHWAY_SIDE_W));
        }
    }

    private void placeParcels(int chunkX,int chunkZ,int chunkSizeX,int chunkSizeZ){

    }


    @Override
    public Parcel getParcel(int chunkX, int chunkZ){
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
//        if(chunkX>=GRID_SIZE || chunkX<0 || chunkZ>=GRID_SIZE || chunkZ<0 ){
//            throw new IndexOutOfBoundsException("Parcel not found in this grid ["+chunkX+"]["+chunkZ+"]");
//        }
        return parcels[x][z];
    }

    @Override
    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        // make sure it's positive and between [0, GRID_SIZE)
        int x = getChunkOffset(chunkX);
        int z = getChunkOffset(chunkZ);
        //if(x>=GRID_SIZE || x<0 || z>=GRID_SIZE || z<0 ) throw new IndexOutOfBoundsException("Parcel not found in this grid ["+chunkX+"]["+chunkZ+"]");

        parcels[x][z] = parcel;

    }

    @Override
    public void fillParcels(int chunkX, int chunkZ, Parcel parcel) {
        for(int x=0;x<parcel.getChunkSizeX();x++){
            for(int z=0;z<parcel.getChunkSizeZ();z++){
                setParcel(chunkX+x,chunkZ+z,parcel);
            }
        }
    }

    /**
     * Calculates the relative coordinates
     * @param chunk
     * @return
     */
    private static int getChunkOffset(int chunk){
        int ret = chunk % GRID_SIZE;
        if(ret<0)
            ret+=GRID_SIZE;
        return ret;
    }
}
