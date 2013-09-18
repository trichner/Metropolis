package ch.k42.metropolis.model;

import ch.k42.metropolis.minions.GridRandom;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 16.09.13
 * Time: 00:45
 * To change this template use File | Settings | File Templates.
 */
public class UrbanGrid extends Grid{
    private Parcel[][] parcels = new Parcel[GRID_SIZE][GRID_SIZE];

    private DistrictParcel district;

    public UrbanGrid(GridProvider provider,GridRandom random,int chunkX,int chunkZ) {
        super(provider,random,chunkX,chunkZ);
        placeHighways();
        district=new DistrictParcel(this,chunkX+1,chunkZ+1,GRID_SIZE-2,GRID_SIZE-2);
        fillParcels(chunkX+1,chunkZ+1,district);
    }

    private void placeHighways(){ // places roads all around the grid
        for(int i=0;i<GRID_SIZE;i++){
            setParcel(0,i,new RoadParcel(this,chunkX,chunkZ+i)); //
            setParcel(i,0,new RoadParcel(this,chunkX+i,chunkZ));
            setParcel(i,GRID_SIZE,new RoadParcel(this,chunkX+i,chunkZ+GRID_SIZE-1));
            setParcel(GRID_SIZE,i,new RoadParcel(this,chunkX+GRID_SIZE-1,chunkZ+i));
        }
    }

    @Override
    public Parcel getParcel(int chunkX, int chunkZ){
        chunkX %= GRID_SIZE;
        chunkZ %= GRID_SIZE;

        if(chunkX>=GRID_SIZE || chunkX<0 || chunkZ>=GRID_SIZE || chunkZ<0 ){
            throw new IndexOutOfBoundsException("Parcel not found in this grid ["+chunkX+"]["+chunkZ+"]");
        }
        return parcels[chunkX][chunkZ];
    }

    @Override
    public void setParcel(int chunkX, int chunkZ, Parcel parcel) {
        chunkX %= GRID_SIZE;
        chunkZ %= GRID_SIZE;

        if(chunkX>=GRID_SIZE || chunkX<0 || chunkZ>=GRID_SIZE || chunkZ<0 ) throw new IndexOutOfBoundsException("Parcel not found in this grid");

        parcels[chunkX][chunkZ] = parcel;

    }

    @Override
    public void fillParcels(int chunkX, int chunkZ, Parcel parcel) {
        for(int x=0;x<parcel.getChunkSizeX();x++){
            for(int z=0;z<parcel.getChunkSizeZ();z++){
                setParcel(chunkX+x,chunkZ+z,parcel);
            }
        }
    }
}
